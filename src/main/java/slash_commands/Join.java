package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Args;
import utils.Embed;

import java.awt.*;

public class Join extends CommandManager {

    public final static SlashCommandData command = Commands.slash("join", "Lets the bot join your voice channel!");

    public boolean execute(SlashCommandInteractionEvent event){
        PlayerManager manager = PlayerManager.getInstance();
        Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());

        if (!event.isAcknowledged()) event.deferReply().queue();

        if (!event.getMember().getVoiceState().inAudioChannel()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not in a voice channel!", Color.RED).build()).queue();
            return false;
        }

        GuildMusicManager guildManager = manager.getGuildMusicManager(event.getGuild());

        // System.out.println("Currently playing track: " + guildManager.player.getPlayingTrack() + " | Is queue empty: " + guildManager.scheduler.getQueue().isEmpty());

        if (guildManager.player.getPlayingTrack() != null || !guildManager.scheduler.getQueue().isEmpty()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "The bot is currently playing something elsewhere", Color.RED).build()).queue();
            return false;
        }
        


        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel());
        audioManager.setSelfDeafened(true);
        event.getHook().editOriginalEmbeds(new Embed("Connected", "Connected to channel " + event.getMember().getVoiceState().getChannel().getName() + "!", Color.GREEN).build()).queue();
        manager.getGuildMusicManager(event.getGuild()).player.setVolume(30);
        guildManager.player.setPaused(false);

        return true;
    }
}
