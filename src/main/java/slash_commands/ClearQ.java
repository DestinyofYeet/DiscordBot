package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Args;

import utils.Constants;
import utils.Embed;

import java.awt.*;

public class ClearQ extends CommandManager {

    public final static SlashCommandData command = Commands.slash("clearqueue", "Lets you clear the current queue!");

    public void execute(SlashCommandInteractionEvent event){
        Constants.deferReplyIfNotAlready(event);

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel() && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel())){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        manager.scheduler.clearQueue();

        event.getHook().editOriginalEmbeds(new Embed("Clear_queue", "Successfully cleared the queue!", Color.GREEN).build()).queue();
    }
}
