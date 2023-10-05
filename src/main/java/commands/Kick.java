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
import java.util.List;

public class Kick extends CommandManager {

    public static final String commandName = "Kick", syntax = "kick (userId) [reason]", description = "Lets you kick a user!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You don't have the permission 'Kick Members'", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an id to ban!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        Member member = Constants.getMemberById(event.getGuild(), argsList.get(0));

        if (member == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Member not found!", Color.RED).build()).queue();
            return;
        }

        String reason = null;

        if (argsList.size() > 1)
            reason = argsList.get(1);

        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());

        if (reason != null){
            member.kick(reason).queue();
            event.getChannel().sendMessageEmbeds(new Embed("Kicked", "Successfully kicked " + member.getUser().getName() + " for `" + reason + "`!", Color.GREEN).build()).queue();
            if (loggingChannel != null)
                loggingChannel.sendMessageEmbeds(new Embed("Kicked", event.getAuthor().getAsMention() + " used this bot to kick " + member.getUser().getName() + " for `" + reason + "`!", Color.BLACK).build()).queue();
        } else {
            member.kick().queue();
            event.getChannel().sendMessageEmbeds(new Embed("Kicked", "Successfully kicked " + member.getUser().getName() + "!", Color.GREEN).build()).queue();
            if (loggingChannel != null)
                loggingChannel.sendMessageEmbeds(new Embed("Kicked", event.getAuthor().getAsMention() + " used this bot to kick " + member.getUser().getName() + "!", Color.BLACK).build()).queue();
        }

    }
}
