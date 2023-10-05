package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Ban extends CommandManager {
    // ban command

    public static final String commandName = "Ban", syntax = "ban (@user / userId) (reason)", description = "Lets you ban a user!";

    public void help(MessageReceivedEvent event){
        commands.Help.send(commandName, syntax,  description, event);
    }

    public void execute (MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions, you don't have the 'ban members' permission!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a member id to ban!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        String idToBan = args.getArgs().get(0);

        UserSnowflake userToBan = User.fromId(idToBan);

        Member member = Constants.getMemberById(event.getGuild(), args.getArgs().get(0));

        String reason = null;
        if (argsList.size() > 1) {
            reason = argsList.get(1);
        }
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());

        final String finalReason = reason;
        event.getGuild().ban(userToBan, 0, TimeUnit.SECONDS).reason("Banned by " + event.getMember().getUser().getName() + " for: " + reason).queue(null, success -> {
            event.getChannel().sendMessageEmbeds(new Embed("User banned", "Successfully banned " + (member != null ? member.getUser().getName() : "user with id" + idToBan) + " for `" + finalReason + "`!", Color.BLACK).build()).queue();
        });



        if (loggingChannel != null){
            loggingChannel.sendMessageEmbeds(new Embed("User banned", event.getAuthor().getName() + " used this bot to ban " + (member != null ? member.getUser().getName() : "a user with id" + idToBan) + " for `" + finalReason + "`!", Color.BLACK).build()).queue();
        }
    }
}
