package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;

import utils.Embed;

import java.awt.*;

public class Skip extends CommandManager {

    public static final String commandName = "Skip", syntax = "skip", description = "Lets you skip the currently playing song!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!Constants.sameChannelAsBot(event.getMember())){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        manager.scheduler.nextTrack();
        event.getChannel().sendMessageEmbeds(new Embed("Skipped", "Successfully skipped current song!", Color.GREEN).build()).queue();
    }
}
