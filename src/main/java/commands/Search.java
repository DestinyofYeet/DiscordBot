package commands;

import audio.PlayerManager;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.stuffs.YoutubeStuff;
import utils.paginator.GenericPaginator;
import utils.paginator.PaginatorEntry;
import utils.paginator.PaginatorReaction;

import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

public class Search extends CommandManager {

    public static final String commandName = "Search",
            syntax = "search (link / searchterm)",
            description = "Lists 5 search results where you can select one for the bot to play!";

    public void execute(MessageReceivedEvent event, Args args){
        Member bot = Constants.getBotUserInGuild(event.getGuild());

        boolean joined = false;

        if (bot.getVoiceState().getChannel() == null){
            if (event.getMember().getVoiceState().getChannel() == null){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to be in a voice channel to run this command!", Color.RED).build()).queue();
                return;
            }

            joined = new Join().execute(event, args);
        }

        if (!joined){
            if (!Constants.sameChannelAsBot(event.getMember())){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not in the same channel as me!", Color.RED).build()).queue();
                return;
            }
        }



        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a search term!", Color.RED).build()).queue();
            return;
        }

        String searchTerm = String.join(" ", args.getArgs());

        SearchListResponse response = null;

        try {
            response = YoutubeStuff.doStuff(searchTerm);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Play command failed because: " + e.getMessage(), Color.RED).build()).queue();
            return;
        }

        if (response == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Api request failed!", Color.RED).build()).queue();
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();

        List<AudioTrack> trackList = new LinkedList<>();

        for (SearchResult result: response.getItems()){

            if (result.getId().getVideoId() == null)
                trackList.add(null);

            else
                trackList.add(manager.getTrackFromUrl("https://youtube.com/watch?v=" + result.getId().getVideoId().trim()));
        }

        GenericPaginator paginator = new GenericPaginator("Search results for \"" + searchTerm + "\"");

        for (AudioTrack track: trackList){
            if (track == null) continue;

            int songSeconds = (int) (track.getDuration() / 1000);
            int minutes = 0;
            int hours = 0;

            if (songSeconds >= 60){
                minutes = songSeconds / 60;
                songSeconds = songSeconds % 60;
            }

            if (minutes >= 60){
                hours = minutes / 60;
                minutes = minutes % 60;
            }


            PaginatorEntry entry = new PaginatorEntry("**Title**: [" + track.getInfo().title + "](" + track.getInfo().uri +  "),\n" +
                    "**Author**: " + track.getInfo().author + ",\n" +
                    "**Duration**: " + (hours > 0 ? hours + " Hours, " : "") + (minutes > 0 ? minutes + " Minutes, ": "") + songSeconds + " Seconds\n\n");

            paginator.addEntry(entry);
        }

        paginator.setMaxElementsPerPage(5);
        paginator.setColor(Color.GREEN);
        paginator.setUserRequestedThis(event.getAuthor());
        paginator.setChannel(event.getChannel());
        paginator.setUseDefaultEmotes(false);

        List<AudioTrack> newTrackList = new LinkedList<>(trackList);

        for (int i = 0; i < trackList.size(); i++){
            if (trackList.get(i) == null) newTrackList.remove(i);
        }


        for (int i = 0; i < newTrackList.size(); i++){
            int j = i;


            paginator.addReaction(new PaginatorReaction((j + 1) + "⃣", ((user, emote, paginator1, message) -> {
                queueSong(j + 1, event, newTrackList, paginator);
            })));
        }

        paginator.addReaction(new PaginatorReaction("❌", (((user, emote, paginator1, message) -> {
            paginator1.close();
        }))));

        paginator.send();
    }

    private void queueSong(int searchIndex, MessageReceivedEvent event, List<AudioTrack> trackList, GenericPaginator paginator){
        PlayerManager manager = PlayerManager.getInstance();

        AudioTrack songToQueue = trackList.get(searchIndex - 1);

        manager.play(manager.getGuildMusicManager(event.getGuild()), songToQueue, false);
        event.getChannel().sendMessageEmbeds(new Embed("Search", "Song Queued!", Color.GREEN).build()).queue();

        paginator.close();
    }
}
