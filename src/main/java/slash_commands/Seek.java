package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Constants;
import utils.Embed;

import java.awt.Color;

public class Seek extends CommandManager {

    public final static SlashCommandData command = Commands.slash("seek", "Lets you skip to a specific time! Format: (hh:mm:ss)")
            .addOption(OptionType.STRING, "time", "The time to seek to! Format: (hh:mm:ss)");

    public void execute(SlashCommandInteractionEvent event){
        Constants.deferReplyIfNotAlready(event);

        String time = Constants.getSlashCommandFieldIfItExistsString(event, "time");

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel() && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel())){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not connected to the voice channel i am connected to!", Color.RED).build()).queue();
            return;
        }

        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        AudioTrack currentTrack = manager.player.getPlayingTrack();

        if (!currentTrack.isSeekable()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "This track is not seekable!", Color.RED).build()).queue();
            return;
        }

        if (time == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a timestamp to skip to!", Color.RED).build()).queue();
            return;
        }

        String[] timeSplit = time.split(":");

        long hours;
        long minutes;
        long seconds;

        try{
            hours = Long.parseLong(timeSplit[0]);
            minutes = Long.parseLong(timeSplit[1]);
            seconds = Long.parseLong(timeSplit[2]);
        } catch (NumberFormatException noted){
            event.getHook().editOriginalEmbeds(new Embed("Error", "Invalid time format! Use hour:minutes:seconds!", Color.RED).build()).queue();
            return;
        }

        long timeToSkipTo = seconds * 1000 + minutes * 60 * 1000 + hours * 60 * 60 * 1000;

        if (timeToSkipTo > currentTrack.getDuration()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are trying to skip over the song length!", Color.RED).build()).queue();
            return;
        }

        currentTrack.setPosition(timeToSkipTo);
        event.getHook().editOriginalEmbeds(new Embed("Seek", "Successfully skipped to " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds!", Color.GREEN).build()).queue();
    }
}
