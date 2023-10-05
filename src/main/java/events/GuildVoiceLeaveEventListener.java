package events;

import audio.GuildMusicManager;
import audio.PlayerManager;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import utils.Constants;

public class GuildVoiceLeaveEventListener extends ListenerAdapter {

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event){
        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()) return;

        // if the bot is alone in a voice channel, stop all music and leave the channel

        if (Constants.getBotUserInGuild(event.getGuild()).getVoiceState().getChannel().getMembers().size() == 1){
            GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.closeAudioConnection();
            manager.player.stopTrack();
            manager.scheduler.clearQueue();
        }

    }
}
