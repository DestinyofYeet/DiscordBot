package slash_commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONObject;
import utils.Constants;
import utils.Embed;
import utils.Logger;
import utils.paginator.GenericPaginator;
import utils.paginator.PaginatorEntry;
import utils.paginator.PaginatorReaction;
import utils.slashpaginator.SlashGenericPaginator;
import utils.slashpaginator.SlashPaginatorEntry;
import utils.slashpaginator.SlashPaginatorReaction;
import utils.uwuwhatsthis_api.requests.doubledouble.DownloadMusicRequest;
import utils.uwuwhatsthis_api.requests.doubledouble.GetDLOptionsRequest;
import utils.uwuwhatsthis_api.requests.doubledouble.GetDownloadStatusRequest;
import utils.uwuwhatsthis_api.requests.doubledouble.SearchMusicRequest;
import utils.uwuwhatsthis_api.requests.doubledouble.results.SongSearchResult;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class DownloadMusic {
    Logger logger = new Logger("DownloadMusic");

    public final static SlashCommandData command = Commands.slash("dl_music", "Downloads a song and adds it to navidrome")
            .addOption(OptionType.STRING, "query", "The query to search for");

    private void do_status_paginator(DownloadMusicRequest request, SlashCommandInteractionEvent event){
        try {
            request.doRequest();
        } catch (IOException e) {
            event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to download music! Network request failed!\n" + e.getMessage(), Color.RED).build()).queue();
            return;
        }

        if (!request.isSuccessful()) {
            event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to download music!\n" + request.getError(), Color.RED).build()).queue();
            return;
        }

        GetDownloadStatusRequest downloadStatusRequest = new GetDownloadStatusRequest(request.getDownloadId());

        try {
            downloadStatusRequest.doRequest();
        } catch (IOException e) {
            event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to get status on download! Network request failed!\n" + e.getMessage(), Color.RED).build()).queue();
            return;
        }

        if (!downloadStatusRequest.isSuccessful()) {
            event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to get status on download!\n" + downloadStatusRequest.getError(), Color.RED).build()).queue();
            return;
        }

        SlashGenericPaginator statusPaginator = new SlashGenericPaginator("Click to refresh the status!");
        statusPaginator.setEvent(event);
        statusPaginator.setColor(Color.GREEN);
        statusPaginator.setMaxElementsPerPage(1);
        statusPaginator.setUserRequestedThis(event.getUser());
        statusPaginator.setUseDefaultEmotes(false);

        statusPaginator.addEntry(new SlashPaginatorEntry(downloadStatusRequest.getStatus()));

        statusPaginator.addReaction(new SlashPaginatorReaction("❌", (((user2, emote2, paginator3, message2) -> {
            paginator3.close();
        }))));

        statusPaginator.addReaction(new SlashPaginatorReaction("\uD83D\uDD01", ((user2, emote2, paginator3, message2) -> {
            paginator3.getPaginatorMessage().removeReaction(Emoji.fromFormatted(emote2), user2).queue();
            try {
                downloadStatusRequest.doRequest();
            } catch (IOException e) {
                paginator3.clearEntries();
                paginator3.addEntry(new SlashPaginatorEntry("Failed to get status on download! Network request failed!\n" + e.getMessage()));
                paginator3.send();
                return;
            }

            if (!downloadStatusRequest.isSuccessful()) {
                paginator3.clearEntries();
                paginator3.addEntry(new SlashPaginatorEntry("Failed to get status on download!\n" + downloadStatusRequest.getError()));
                paginator3.send();
                return;
            }

            paginator3.clearEntries();
            paginator3.addEntry(new SlashPaginatorEntry("Download Status: " + downloadStatusRequest.getStatus()));
            paginator3.send();

            if (Objects.equals(downloadStatusRequest.getStatus(), "Done!")) {
                paginator3.clearReactions();
            }
        })));

        statusPaginator.send();
    }

    public void execute(SlashCommandInteractionEvent event) {
        if (!Constants.getTrustedIds().contains(event.getInteraction().getMember().getUser().getIdLong())) {
            event.replyEmbeds(new Embed("Error", "You are not authorized to run this command!", Color.RED).build()).setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        String query = Constants.getSlashCommandFieldIfItExistsString(event, "query");

        event.getHook().editOriginalEmbeds(new Embed("Download Music", "Searching for: " + query, Color.GREEN).build()).queue();

        SearchMusicRequest request = new SearchMusicRequest(query);

        try {
            request.doRequest();
        } catch (IOException e) {
            event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to search for music! Network request failed!\n" + e.getMessage(), Color.RED).build()).queue();
            return;
        }

        if (!request.isSuccessful()) {
            try {
                event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to search for music!\n" + request.getError(), Color.RED).build()).queue();
            } catch (IllegalArgumentException e) {
                event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to search for music!", Color.RED).build()).queue();
                event.getHook().editOriginalAttachments(FileUpload.fromData(
                        request.getError().getBytes(StandardCharsets.UTF_8),
                        "Error.txt"
                )).queue();
            }
            return;
        }

        ArrayList<SongSearchResult> results = request.getResults();

        SlashGenericPaginator paginator = new SlashGenericPaginator("Music Search Results");
        for (SongSearchResult entry : results) {
            paginator.addEntry(new SlashPaginatorEntry("**Title:** " + entry.getName() + "\n" +
                    "**Artist:** " + entry.getArtist() + "\n" +
                    "**Album:** " + entry.getAlbum())

                    .setCoverUrl(entry.getCoverUrl())
            );
        }

        paginator.addReaction(new SlashPaginatorReaction("✅", ((user, emote, paginator1, message) -> {
            paginator1.close();
            paginator1.clearReactions();
//            logger.info("Searching for download options!");

            event.getHook().editOriginalEmbeds(new Embed("Download options", "Getting download options...", Color.GREEN).build()).queue();

            int entry = paginator1.getCurrentPage() - 1;

            SongSearchResult selectedResult = results.get(entry);


            GetDLOptionsRequest dlOptionsRequest = new GetDLOptionsRequest(selectedResult.getLinks());


            try {
                dlOptionsRequest.doRequest();
            } catch (IOException e) {
                event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to search for download options! Network request failed!\n" + e.getMessage(), Color.RED).build()).queue();
                return;
            }


            if (!dlOptionsRequest.isSuccessful()) {
                if (Objects.equals(dlOptionsRequest.getError(), "Failed to download options: No entries could be found! (Site down?)")){
                    logger.info("Failed to find music, downloading using initial spotify link!");
                    event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to find download links. Downloading from spotify!", Color.ORANGE).build()).queue();
                    DownloadMusicRequest spotifyBackupDownloadMusic = new DownloadMusicRequest(selectedResult.getLink(), "spotify");
                    do_status_paginator(spotifyBackupDownloadMusic, event);
                    return;

                }
                event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to search for download options!\n" + dlOptionsRequest.getError(), Color.RED).build()).queue();
                return;
            }

//            logger.info("Request successful!");

            JSONObject dlOptions = dlOptionsRequest.getUrls().getJSONObject("results");

            SlashGenericPaginator downloadPaginator = new SlashGenericPaginator("Pick a download platform!");
            downloadPaginator.setEvent(event);
            downloadPaginator.setColor(Color.GREEN);
            downloadPaginator.setMaxElementsPerPage(dlOptions.length());
            downloadPaginator.setUserRequestedThis(event.getUser());
            downloadPaginator.setUseDefaultEmotes(false);
            downloadPaginator.setClosedTitle("Download");
            downloadPaginator.setClosedMessage("Starting download...");

            HashMap<String, String> emote_download_map = new HashMap<>();
            int index = -1;
            for (Iterator<String> it = dlOptions.keys(); it.hasNext(); ) {
                String dlOption = it.next();
                index++;

                String emojii = index + "️⃣";

                emote_download_map.put(emojii, dlOption);

                downloadPaginator.addEntry(new SlashPaginatorEntry(emojii + ": " + dlOption));
                downloadPaginator.addReaction(new SlashPaginatorReaction(emojii, ((user1, emote1, paginator2, message1) -> {

                    paginator2.clearReactions();
                    paginator2.close();

                    String downloadPlatform = emote_download_map.get(emote1);
                    String url = dlOptions.getString(downloadPlatform);

                    DownloadMusicRequest musicRequest = new DownloadMusicRequest(url, downloadPlatform);

                    do_status_paginator(musicRequest, event);

                })));
            }
            downloadPaginator.send();
        })));

        paginator.setEvent(event);
        paginator.setColor(Color.GREEN);
        paginator.setUserRequestedThis(event.getUser());
        paginator.setMaxElementsPerPage(1);
        paginator.send();
    }
}
