package slash_commands;

import audio.GuildMusicManager;
import audio.PlayerManager;
import main.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Args;

import utils.Constants;
import utils.Embed;

import java.awt.Color;

public class Volume extends CommandManager {

    public static final String commandName = "Volume", syntax = "volume [new volume]", description = "Lets you either see the current volume or provide a new one. It's recommended to set the volume between 0-100.";

    public final static SlashCommandData command = Commands.slash("volume", "Lets you either see the current volume or provide a new one. Recommended: Between 1-100%")
            .addOption(OptionType.INTEGER, "volume", "The amount of volume.");


    public void execute(SlashCommandInteractionEvent event){
        Constants.deferReplyIfNotAlready(event);

        String volume = Constants.getSlashCommandFieldIfItExistsString(event, "volume");

        if (!event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().inAudioChannel()){
            event.getHook().editOriginalEmbeds(new Embed("Error", "The bot is not in a voice channel!", Color.RED).build()).queue();
            return;
        }
        boolean memberIsInAChannel = event.getMember().getVoiceState().inAudioChannel();
        if (!memberIsInAChannel){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not in a voice channel!", Color.RED).build()).queue();
            return;
        }
        boolean memberIsInSameChannel = event.getMember().getVoiceState().getChannel().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getVoiceState().getChannel());

        if (!memberIsInSameChannel){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You are not in the same voice channel the bot is in!", Color.RED).build()).queue();
            return;
        }
        GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());

        if (volume == null){
            event.getHook().editOriginalEmbeds(new Embed("Volume", "Current volume is: " + manager.player.getVolume() + "%!", Color.GREEN).build()).queue();
            return;
        }

        manager.player.setVolume(Integer.parseInt(volume));
        event.getHook().editOriginalEmbeds(new Embed("Skipped", "Successfully set volume to " + volume + "%!", Color.GREEN).build()).queue();
    }
}
