package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import main.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Args;

import utils.Constants;
import utils.Embed;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NowPlaying extends CommandManager {

    public final static SlashCommandData command = Commands.slash("now_playing", "Lets you see the current playing song!");

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
        AudioTrack currentTrack = manager.player.getPlayingTrack();

        if (currentTrack == null){
            event.getHook().editOriginalEmbeds(new Embed("Error", "Nothing is playing at the moment!", Color.RED).build()).queue();
            return;
        }

        String songName = currentTrack.getInfo().title;
        String songUrl = currentTrack.getInfo().uri;
        long totalLength = currentTrack.getDuration();
        long currentlyAt = currentTrack.getPosition();




        EmbedBuilder embed = new EmbedBuilder();
        embed.setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
        embed.setTitle("Now playing", songUrl);
        embed.addField("Song name:", songName, true);
        embed.addField("Song duration:", Constants.convertLongLengthInStringLength(currentlyAt)  + " / " + Constants.convertLongLengthInStringLength(totalLength), true);
        embed.setColor(Color.GREEN);

        event.getHook().editOriginalEmbeds(embed.build()).queue();

    }
}
