package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.*;
import utils.stuffs.JsonStuff;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class WordBlacklist extends CommandManager {

    public static final String commandName = "Wordblacklist", syntax = "wordblacklist (add / remove / list) [word]", description = "Lets you blacklist a word. Be careful! If you blacklist the character 'a', nobody will be a ble to send a message contain the character 'a'!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the 'Administrator' permission!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an option!", Color.RED).build()).queue();
            return;
        }

        List<String> blacklistedWords = JsonStuff.getStringListFromJson(Constants.getBlacklistedWordsPath(), event.getGuild().getId());

        if (args.get(0).equalsIgnoreCase("list")){
            String content = null;
            if (blacklistedWords != null){
                content = String.join(", ", blacklistedWords);
                event.getChannel().sendMessageEmbeds(new Embed("List", "The following words are blacklisted from your server!\n\n" + content, Color.GREEN).build()).queue();
            } else {
                event.getChannel().sendMessageEmbeds(new Embed("List", "You have no blacklisted words!", Color.GREEN).build()).queue();
            }
            return;
        }

        if (blacklistedWords == null){
            blacklistedWords = new ArrayList<String>();
        }

        if (args.size() < 2){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an argument!", Color.RED).build()).queue();
            return;
        }

        String option = args.get(0);

        if (option.equalsIgnoreCase("add")){
            String wordToAdd = args.get(1);

            if (blacklistedWords.contains(wordToAdd)){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "That word is already blacklisted!", Color.RED).build()).queue();
                return;

            } else {
                blacklistedWords.add(wordToAdd);
                event.getChannel().sendMessageEmbeds(new Embed("Blacklisted", "The word '" + wordToAdd + "' is now blacklisted!", Color.GREEN).build()).queue();
            }
            JsonStuff.writeStringListToJsonFile(Constants.getBlacklistedWordsPath(), event.getGuild().getId(), blacklistedWords);

        } else if (option.equalsIgnoreCase("remove")){
            String wordToRemove = args.get(1);

            if (!blacklistedWords.contains(wordToRemove)){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "That word is not blacklisted!", Color.RED).build()).queue();
                return;

            } else{
                blacklistedWords.remove(wordToRemove);
                event.getChannel().sendMessageEmbeds(new Embed("Unblacklisted", "The word '" + wordToRemove + "' is now unblacklisted!", Color.GREEN).build()).queue();
            }
            JsonStuff.writeStringListToJsonFile(Constants.getBlacklistedWordsPath(), event.getGuild().getId(), blacklistedWords);

        } else {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid option!", Color.RED).build()).queue();
        }

    }
}
