package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Constants;
import utils.Embed;
import utils.stuffs.YoutubeStuff;

import java.awt.*;

public class Play extends CommandManager {

    public final static SlashCommandData command = Commands.slash("play", "Plays either a searched term from youtube or play from a link.")
            .addOption(OptionType.STRING, "query", "The query to search for or the link!");


    public void execute(SlashCommandInteractionEvent event){
        execute(event,false);
    }

    public void execute(SlashCommandInteractionEvent event, boolean insertTop){
        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager directManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());

        if (!event.isAcknowledged()) event.deferReply().queue();

        String searchTerm = Constants.getSlashCommandFieldIfItExistsString(event, "query");

        if (searchTerm == null){
            if (directManager.player.isPaused()){
                directManager.player.setPaused(false);
                event.getHook().editOriginalEmbeds(new Embed("Unpaused", "The bot is now unpaused!", Color.GREEN).build()).queue();
                return;
            }
            event.getHook().editOriginalEmbeds(new Embed("Error", "Please provide a valid url or search term!", Color.RED).build()).queue();
            return;
        }

        if (event.getMember().getVoiceState().inAudioChannel()){

            if (!Constants.sameChannelAsBot(event.getMember())){
                Join join = new Join();

                if (!join.execute(event)){
                    // if switch was successful
                    return;
                }

            }

        }else{
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not in a voice channel!", Color.RED).build()).queue();
            return;
        }


//        System.out.println("Play: Args.get(0)=" + args.get(0));



        if (Constants.isUrl(searchTerm)){
            boolean canSeek = false;
            int realTime = 0;
            if (searchTerm.contains("&t=")){
                String timeToSkip = searchTerm.split("&t=")[1];
                if (timeToSkip.contains("&")){
                    timeToSkip = timeToSkip.split("&")[0];
                }
                try{
                    realTime = Integer.parseInt(timeToSkip);
                    canSeek = true;
                } catch (NumberFormatException e){
                    event.getHook().editOriginalEmbeds(new Embed("Error", "Could not parse timestamp, skipping auto-seek!", Color.RED).build()).queue();
                }
            } else if (searchTerm.contains("?t=")){
                String timeToSkip = searchTerm.split("t=")[1];
                try{
                    realTime = Integer.parseInt(timeToSkip);
                    canSeek = true;
                } catch (NumberFormatException e){
                    event.getHook().editOriginalEmbeds(new Embed("Error", "Could not parse timestamp, skipping auto-seek!", Color.RED).build()).queue();
                }
            }

            manager.loadAndPlay(event, searchTerm, insertTop);
            if (canSeek && !(realTime == 0)){
                GuildMusicManager realManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
                AudioTrack currentTrack = realManager.player.getPlayingTrack();
                while (currentTrack == null){
                    currentTrack = realManager.player.getPlayingTrack();
                }
                currentTrack.setPosition((long) realTime * 1000);
                event.getHook().editOriginalEmbeds(new Embed("Auto-Seek", "Timestamp detected. Skipping to timestamp!", Color.GREEN).build()).queue();
            }
            return;
        }

//        System.out.println("Play: Searching on yt");

        // if the input is not a URL, it joins all arguments to a string and searches per Youtube-API for it

        try{
            SearchListResponse response = YoutubeStuff.doStuff(searchTerm);
            ResourceId id = response.getItems().get(0).getId();
            manager.loadAndPlay(event, "https://youtube.com/watch?v=" + id.getVideoId().trim(), insertTop);
        } catch (Exception e){
            event.getHook().editOriginalEmbeds(new Embed("Error", "Play command failed because: " + e.getMessage(), Color.RED).build()).queue();
        }
    }
}
