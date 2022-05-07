package commands;

import main.CommandManager;
import utils.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.stuffs.PrefixStuff;

import java.awt.*;
import java.util.List;

public class Prefix extends CommandManager {

    public static final String commandName = "Prefix", syntax = "prefix (new prefix)", description = "Lets you set a new prefix for your guild!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need the Administrator permission for this command to work!", event.getMember().getColor()).build()).queue();
        }

        List<String> argsList = args.getArgs();

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Prefix", "Your current prefix is " + PrefixStuff.getPrefix(event.getGuild().getIdLong()), Color.BLACK).build()).queue();
            return;
        }

        if (PrefixStuff.getPrefix(event.getGuild().getIdLong()).equals(argsList.get(0))){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The prefix is the same!", Color.RED).build()).queue() ;
            return;
        }

        PrefixStuff.setPrefix(event.getGuild().getIdLong(), argsList.get(0));
        event.getChannel().sendMessageEmbeds(new Embed("New prefix set!", "Your new prefix is now " + argsList.get(0), Color.GREEN).build()).queue();

    }
}
