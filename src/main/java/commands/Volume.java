package commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;

import utils.Embed;

import java.awt.Color;

public class Volume extends CommandManager {

    public static final String commandName = "Volume", syntax = "volume [new volume]", description = "Lets you either see the current volume or provide a new one. It's recommended to set the volume between 0-100.";


    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }
        boolean memberIsInAChannel = event.getMember().getVoiceState().inAudioChannel();
        if (!memberIsInAChannel){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not in a voice channel!", Color.RED).build()).queue();
            return;
        }
        boolean memberIsInSameChannel = event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel());

        if (!memberIsInSameChannel){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not in the same voice channel the bot is in!", Color.RED).build()).queue();
            return;
        }
        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Volume", "Current volume is: " + manager.player.getVolume() + "%!", Color.GREEN).build()).queue();
            return;
        }

        manager.player.setVolume(Integer.parseInt(args.get(0)));
        event.getChannel().sendMessageEmbeds(new Embed("Skipped", "Successfully set volume to " + args.get(0) + "%!", Color.GREEN).build()).queue();
    }
}
