package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.stuffs.YoutubeStuff;

import java.awt.*;
import java.util.List;

public class Play extends CommandManager {

    public static final String commandName = "Play",
            syntax = "play (link / searchterm)",
            description = "Lets the bot either search from youtube or play from a link. Has to be supported by [this lavaplayer fork](https://github.com/Walkyst/lavaplayer-fork#supported-formats)!";


    public void execute(MessageReceivedEvent event, Args args){
        execute(event, args, false);
    }

    public void execute(MessageReceivedEvent event, Args args, boolean insertTop){
        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager directManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());

        if (args.isEmpty()){
            if (directManager.player.isPaused()){
                directManager.player.setPaused(false);
                event.getChannel().sendMessageEmbeds(new Embed("Unpaused", "The bot is now unpaused!", Color.GREEN).build()).queue();
                return;
            }
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Please provide a valid url or search term!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        if (event.getMember().getVoiceState().inAudioChannel()){

            if (!Constants.sameChannelAsBot(event.getMember())){
                Join join = new Join();

                if (!join.execute(event, args)){
                    // if switch was successful
                    return;
                }

            }

        }else{
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not in a voice channel!", Color.RED).build()).queue();
            return;
        }



        if (Constants.isUrl(argsList.get(0))){
            boolean canSeek = false;
            int realTime = 0;
            String url = argsList.get(0);
            if (url.contains("&t=")){
                String timeToSkip = url.split("&t=")[1];
                if (timeToSkip.contains("&")){
                    timeToSkip = timeToSkip.split("&")[0];
                }
                try{
                    realTime = Integer.parseInt(timeToSkip);
                    canSeek = true;
                } catch (NumberFormatException e){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "Could not parse timestamp, skipping auto-seek!", Color.RED).build()).queue();
                }
            } else if (url.contains("?t=")){
                String timeToSkip = url.split("t=")[1];
                try{
                    realTime = Integer.parseInt(timeToSkip);
                    canSeek = true;
                } catch (NumberFormatException e){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "Could not parse timestamp, skipping auto-seek!", Color.RED).build()).queue();
                }
            }

            manager.loadAndPlay(event.getTextChannel(), argsList.get(0), insertTop);
            if (canSeek && !(realTime == 0)){
                GuildMusicManager realManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
                AudioTrack currentTrack = realManager.player.getPlayingTrack();
                while (currentTrack == null){
                    currentTrack = realManager.player.getPlayingTrack();
                }
                currentTrack.setPosition((long) realTime * 1000);
                event.getChannel().sendMessageEmbeds(new Embed("Auto-Seek", "Timestamp detected. Skipping to timestamp!", Color.GREEN).build()).queue();
            }
            return;
        }


        // if the input is not a URL, it joins all arguments to a string and and searches per Youtube-API for it
        String input = String.join(" ", argsList);

        try{
            SearchListResponse response = YoutubeStuff.doStuff(input);
            ResourceId id = response.getItems().get(0).getId();
            manager.loadAndPlay(event.getTextChannel(), "https://youtube.com/watch?v=" + id.getVideoId().trim(), insertTop);
        } catch (Exception e){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Play command failed because: " + e.getMessage(), Color.RED).build()).queue();
        }
    }
}
