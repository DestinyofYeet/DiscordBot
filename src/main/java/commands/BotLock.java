package commands;

import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.Color;
import java.util.List;

public class BotLock extends CommandManager {
    // Locks the bot for any further use

    public static final String commandName = "Botlock", syntax = "botlock (on / off)", description = "Lets you lock the bot commands. You need to be trusted to run this command";

    public void execute(MessageReceivedEvent event, Args args){
        if (!Constants.getTrustedIds().contains(event.getMember().getIdLong())){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are not trusted to do this!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Provide on option: on / off", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        boolean whatDo;

        if (argsList.get(0).equalsIgnoreCase("on")){
            whatDo = true;
        } else if (argsList.get(0).equalsIgnoreCase("off")){
            whatDo = false;
        } else {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid option!", Color.RED).build()).queue();
            return;
        }

        if (CommandManager.getBotIsLocked()){
            if (whatDo){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "Bot is already locked", Color.RED).build()).queue();
            } else {
                CommandManager.setBotIsLocked(false);
                event.getChannel().sendMessageEmbeds(new Embed("Bot lock", "The bot is now unlocked!", Color.GREEN).build()).queue();
            }

        } else {
            if (!whatDo){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "Bot is already unlocked", Color.RED).build()).queue();
            } else {
                CommandManager.setBotIsLocked(true);
                event.getChannel().sendMessageEmbeds(new Embed("Bot lock", "The bot is now locked!", Color.GREEN).build()).queue();
            }
        }
    }
}
