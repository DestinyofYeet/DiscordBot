package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.RestAction;
import utils.Args;
import utils.Constants;

import utils.Embed;

import java.awt.Color;

public class Unban extends CommandManager {

    public static final String commandName = "Unban", syntax = "unban (userId)", description = "Lets you unban a banned user!";


    public void execute(MessageReceivedEvent event, Args args){
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel == null) return;

        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the 'Ban members' permission to unban somebody", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an id of the member you are trying to unban!", Color.RED).build()).queue();
            return;
        }

        RestAction<Guild.Ban> banData;

        try{
            banData = event.getGuild().retrieveBanById(args.get(0));
        } catch (IllegalArgumentException e){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid input. You need to provide a discord user id!", Color.RED).build()).queue();
            return;
        }

        Guild.Ban ban;

        try{
            ban = banData.complete();
        } catch (ErrorResponseException e){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That user isn't banned!", Color.RED).build()).queue();
            return;
        }

        event.getGuild().unban(ban.getUser()).queue();

        event.getChannel().sendMessageEmbeds(new Embed("Unbanned", "Successfully unbanned " + ban.getUser().getName() + "!", Color.GREEN).build()).queue();
    }
}
