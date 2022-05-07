package commands;

import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Embed;

import java.awt.*;

public class Invite extends CommandManager {
    // if someone wants an invite, give it to them

    public final static String commandName = "Invite", syntax = "invite", description = "Lets you invite the bot to other servers!";

    public void execute(MessageReceivedEvent event, Args args){
        String inviteLink = "https://discord.com/api/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&permissions=8&scope=bot%20applications.commands";
        event.getChannel().sendMessageEmbeds(new Embed("Invite", "[Click to invite me](" + inviteLink + ")", Color.GREEN).build()).queue();

    }
}
