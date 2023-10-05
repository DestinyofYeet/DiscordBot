package audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import utils.Embed;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {


    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager(){
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild){
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null){
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(SlashCommandInteractionEvent event, String trackURL){
        loadAndPlay(event, trackURL, false);
    }

    public void loadAndPlay(SlashCommandInteractionEvent event, String trackURL, boolean insertTop){
        GuildMusicManager musicManager = getGuildMusicManager(event.getGuild());

        playerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                event.getHook().editOriginalEmbeds(new Embed("Queued!", "Added " + audioTrack.getInfo().title + " to queue!", Color.GREEN).build()).queue();

                play(musicManager, audioTrack, insertTop);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();

                if (firstTrack == null){
                    for (AudioTrack audioTrack: audioPlaylist.getTracks()){
                        play(musicManager, audioTrack, insertTop);
                    }
                    event.getHook().editOriginalEmbeds(new Embed("Play", "Playlist \"" + audioPlaylist.getName() + "\" loaded with " + audioPlaylist.getTracks().size() + " entries!", Color.GREEN).build()).queue();
                    return;
                }

                event.getHook().editOriginalEmbeds(new Embed("Queued!", "Added \"" + firstTrack.getInfo().title + "\" to queue!", Color.GREEN).build()).queue();

                play(musicManager, firstTrack, insertTop);
            }

            @Override
            public void noMatches() {
                event.getHook().editOriginalEmbeds(new Embed("Error", "Nothing found by searching for " + trackURL, Color.RED).build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.getHook().editOriginalEmbeds(new Embed("Error", "Could not play: " + e.getMessage(), Color.RED).build()).queue();
            }
        });
    }

    public void play(GuildMusicManager musicManager, AudioTrack track, boolean insertTop){
        musicManager.scheduler.queue(track, insertTop);
    }

    public AudioTrack getTrackFromUrl(String url) {

        final AudioTrack[] result = {null};

        Object lock = new Object();

        synchronized (lock){
            playerManager.loadItem(url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    result[0] = audioTrack;

                    synchronized (lock){
                        lock.notify();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
                    result[0] = firstTrack;

                    synchronized (lock){
                        lock.notify();
                    }
                }

                @Override
                public void noMatches() {
                    synchronized (lock){
                        lock.notify();
                    }
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    synchronized (lock){
                        lock.notify();
                    }
                }
            });

            try{
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        return result[0];
    }


    public static synchronized PlayerManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
