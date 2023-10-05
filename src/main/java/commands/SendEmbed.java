package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.*;

public class SendEmbed extends CommandManager {
    private static Permission permissionNeeded = Permission.ADMINISTRATOR;

    public final static String commandName = "Send embed", syntax = "send_embed (channelId / #channel) (Title) (Description)", description = "Lets you send an embed into a channel. Put the title and description in quotes to have spaces in them!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permissionNeeded)){
            event.getChannel().sendMessageEmbeds(new Embed("Insufficient permissions!", "You need the " + permissionNeeded.toString() + " permission to use this command!", Color.RED).build()).queue();
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a channel to send the embed in!", Color.RED).build()).queue();
            return;
        }

        String channelInput = args.get(0);

        if (args.size() < 2) {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a title for the embed!", Color.RED).build()).queue();
            return;
        }

        String title = args.get(1);

        if (args.size() < 3){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide some text to put in the embed body!", Color.RED).build()).queue();
            return;
        }

        String body = args.get(2);

        TextChannel channel = Constants.getTextChannel(event.getGuild(), channelInput);

        if (channel == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That channel doesn't exist!", Color.RED).build()).queue();
            return;
        }

        channel.sendMessageEmbeds(new Embed(title, body, event.getMember().getColor()).build()).queue(
                (message) ->
                    event.getChannel().sendMessageEmbeds(new Embed("Send Embed", "Successfully send embed!\n\n [Click to jump to the embed](" + message.getJumpUrl() + ")", Color.GREEN).build()).queue()
        );
    }
}