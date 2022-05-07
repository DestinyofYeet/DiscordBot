package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;

import utils.Embed;

import java.awt.*;

public class ClearQ extends CommandManager {
    // clears the current music queue

    public static final String commandName = "clear_queue", syntax = "clear_queue", description = "Lets you clear the current queue!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel() && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel())){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        manager.scheduler.clearQueue();

        event.getChannel().sendMessageEmbeds(new Embed("Clear_queue", "Successfully cleared the queue!", Color.GREEN).build()).queue();
    }
}
