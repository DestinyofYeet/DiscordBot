package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Args;
import utils.Embed;

import java.awt.*;

public class Disconnect extends CommandManager {
    // disconnects from a channel

    public final static SlashCommandData command = Commands.slash("disconnect", "Disconnects the bot from your voice channel!");

    public void execute(SlashCommandInteractionEvent event){
        if (!event.isAcknowledged()) event.deferReply().queue();

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel() && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel())){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
        event.getHook().editOriginalEmbeds(new Embed("Disconnected", "Disconnected from the voice channel!", Color.GREEN).build()).queue();
        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
        manager.player.stopTrack();
        manager.scheduler.clearQueue();
    }
}
