package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

public class VcTimeOut extends CommandManager {

    public static final String commandName = "Vctimeout", syntax = "vctimeout (@user / userId) (minutes)", description = "Lets you vc-mute a member!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient Permissions! You need the 'Mute others' permission!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to specify a user to mute!", Color.RED).build()).queue();
            return;
        }

        String memberId = args.get(0);
        Member member = Constants.getMemberById(event.getGuild(), memberId);
        if (member.getVoiceState().isGuildMuted()){
            member.mute(false).queue();
            event.getChannel().sendMessageEmbeds(new Embed("Timeout", "Timeout for " + member.getAsMention() + " has been revoked!", Color.GREEN).build()).queue();
            return;
        }

        if (args.size() < 2){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an amount of minutes to mute the member!", Color.RED).build()).queue();
            return;
        }

        long amountOfMins = Long.parseLong(args.get(1));
        if (member == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That member doesn't exist!", Color.RED).build()).queue();
            return;
        }

        if (!member.getVoiceState().inAudioChannel()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The member is not connected to a voice channel!", Color.RED).build()).queue();
            return;
        }

        if (member.getVoiceState().isGuildMuted()){
            member.mute(false).queue();
            event.getChannel().sendMessageEmbeds(new Embed("Timeout", "Timeout for " + member.getAsMention() + " has been revoked!", Color.GREEN).build()).queue();
            return;
        }

        member.mute(true).queue();
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel != null){
            loggingChannel.sendMessageEmbeds(new Embed("Timout", member.getAsMention() + " has received a timeout from " + event.getMember().getAsMention() + " for " + amountOfMins + " minutes!", Color.BLACK).build()).queue();
            loggingChannel.sendMessage("```Timeout for " + member.getEffectiveName() + " (" + member.getId() + ") has finished!```").queueAfter(amountOfMins, TimeUnit.MINUTES);
        } else {
            event.getChannel().sendMessage("```Timeout for " + member.getEffectiveName() + " (" + member.getId() + ") has finished!```").queueAfter(amountOfMins, TimeUnit.MINUTES);
        }
        event.getChannel().sendMessageEmbeds(new Embed("Timeout", member.getAsMention() + " has received a timeout for " + amountOfMins + " minutes!\n\n The user will be unmuted automatically! If the user leaves the voice channel, use `vctimeout @user` to unmute the user again, or unmute the user manually!", Color.GREEN).build()).queue();
    }
}
