package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;

import utils.Embed;

import java.awt.*;

public class Pause extends CommandManager {

    public static final String commandName = "Pause", syntax = "pause", description = "Lets you pause the playback. Do pause or play again to unpause the bot!";

    public void execute (MessageReceivedEvent event, Args args){
        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel() && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel())){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if (manager.player.getPlayingTrack() == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Nothing is currently playing!", Color.RED).build()).queue();
            return;
        }
        if (manager.player.isPaused()){
            event.getChannel().sendMessageEmbeds(new Embed("Unpaused", "Bot is now unpaused!", Color.GREEN).build()).queue();
            manager.player.setPaused(false);
            return;
        }
        manager.player.setPaused(true);

        event.getChannel().sendMessageEmbeds(new Embed("Paused", "The bot is now paused!", Color.GREEN).build()).queue();
    }
}
