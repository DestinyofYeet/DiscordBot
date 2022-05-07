package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.*;

public class Shuffle extends CommandManager {

    public static final String commandName = "Shuffle", syntax = "shuffle", description = "Shuffles the queue!";

    public Shuffle(){

    }

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


        if (manager.scheduler.getQueue().size() <= 1){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need more than 1 song in your queue to use this!", Color.RED).build()).queue();
            return;
        }

        manager.scheduler.shuffleQueue();

        event.getChannel().sendMessageEmbeds(new Embed("Shuffled", "Successfully shuffled queue!", Color.GREEN).build()).queue();
    }
}
