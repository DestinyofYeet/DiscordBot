package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.*;

public class Shuffle extends CommandManager {

    public static final String commandName = "Shuffle", syntax = "shuffle", description = "Shuffles the queue!";

    public final static SlashCommandData command = Commands.slash("shuffle", "Shuffles the queue!");

    public Shuffle(){

    }

    public void execute(SlashCommandInteractionEvent event){
        Constants.deferReplyIfNotAlready(event);

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!Constants.sameChannelAsBot(event.getMember())){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());


        if (manager.scheduler.getQueue().size() <= 1){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You need more than 1 song in your queue to use this!", Color.RED).build()).queue();
            return;
        }

        manager.scheduler.shuffleQueue();

        event.getHook().editOriginalEmbeds(new Embed("Shuffled", "Successfully shuffled queue!", Color.GREEN).build()).queue();
    }
}
