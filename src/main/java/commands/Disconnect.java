package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Args;
import utils.Embed;

import java.awt.*;

public class Disconnect extends CommandManager {
    // disconnects from a channel

    public static final String commandName = "Disconnect", syntax = "disconnect", description = "Disconnects the bot from it's current voice channel!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel() && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel())){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
        event.getChannel().sendMessageEmbeds(new Embed("Disconnected", "Disconnected from the voice channel!", Color.GREEN).build()).queue();
        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        manager.player.stopTrack();
        manager.scheduler.clearQueue();
    }
}
