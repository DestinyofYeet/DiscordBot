package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;

import utils.Embed;
import utils.paginator.GenericPaginator;
import utils.paginator.PaginatorEntry;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;

public class Queue extends CommandManager {

    public final static String commandName = "Queue", syntax = "queue", description = "Lets you see whats in the queue!";

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
        BlockingQueue<AudioTrack> queue = manager.scheduler.getQueue();

        GenericPaginator paginator = new GenericPaginator("Queue (total " + queue.size() + " songs)");

        for (AudioTrack track: queue){
            paginator.addEntry(new PaginatorEntry("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"));
        }

        paginator.setUserRequestedThis(event.getMember().getUser());
        paginator.setColor(Color.GREEN);
        paginator.setChannel(event.getChannel());
        paginator.setMaxElementsPerPage(10);

        paginator.send();
    }
}
