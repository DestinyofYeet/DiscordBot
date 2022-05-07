package commands;

import main.CommandManager;
import utils.Args;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import utils.Embed;

import java.awt.*;
import java.util.List;

public class Clear extends CommandManager {
    // deletes a certain number of messages in a channel

    public static final String commandName = "Clear", syntax = "clear [amount of messages]", description = "Lets you specify the amount of messages the bot should delete";

    private static final Permission permission = Permission.MESSAGE_MANAGE;

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permission)){
            event.getChannel().sendMessageEmbeds(new Embed("Insufficient permissions!", "You don't have the '"+ permission.toString() + "' permission!", Color.RED).build()).queue();
            return;
        }

        int messageAmount = 0;

        List<String> argsList = args.getArgs();

        if (args.isEmpty()){
            messageAmount = 20 + 1;

        } else {
            messageAmount = Integer.parseInt(argsList.get(0)) + 1;
        }

        try {
            MessageHistory history = new MessageHistory(event.getChannel());
            List<Message> msg;

            msg = history.retrievePast(messageAmount).complete();

            event.getChannel().purgeMessages(msg);
            event.getChannel().sendMessageEmbeds(new Embed("Clear", messageAmount - 1 + " messages have been deleted!", Color.GREEN).build()).queue();

        } catch (InsufficientPermissionException exception){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Could not clear messages due to not having 'manage messages' permission!", Color.RED).build()).queue();
        }

    }
}
