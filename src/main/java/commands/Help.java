package commands;

import events.PaginatorEventListener;
import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.paginator.GenericPaginator;
import utils.paginator.PaginatorEntry;
import utils.paginator.PaginatorReaction;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.LinkedList;


public class Help extends CommandManager {
    public static final String[] MODERATION_COMMANDS_RAW = {
            "Ban", "BotLock", "Exclude", "History", "Invite", "InviteWhitelist", "Kick", "LoggingChannel", "MessageCache",
            "Poll", "Prefix", "RemoveWarn", "Restart", "RoleReact", "SendEmbed", "Setup", "Unban", "UserInfo", "VcTimeOut",
            "Warn", "WordBlacklist", "Clear", "Ping", "VerificationLevelCommand"
    };

    public static final String[] MUSIC_COMMANDS_RAW = {
            "Recognize"
    };

    public static final LinkedList<String> MODERATION_COMMANDS = new LinkedList<>(), MUSIC_COMMANDS = new LinkedList<>();

    private final StringBuilder helpTextBuilder = new StringBuilder()
            .append("Select a category of commands you want help for!").append("\n\n")
            .append("\uD83D\uDEE1️**: Moderation**").append("\n")
            .append("\uD83C\uDFB5**: Music**").append("\n")
            .append("❌**: Close this window**").append("\n")
            .append("\n")
            .append("Arguments wrapped in () are **needed**!").append("\n")
            .append("Arguments wrapped in [] are **optional**!").append("\n\n\n")
            .append("Do `help command` to get more information about a command!");

    public static void send(String commandName, String syntax, String description, MessageReceivedEvent event){
        event.getChannel().sendMessageEmbeds(new Embed(commandName, "Syntax:```\n" + syntax + "```\n" + description, Color.WHITE).build()).queue();
    }

    private void getHelpInfoAndAddToList(String className, LinkedList<String> whichListToAdd){
        try {
            Class c = Class.forName("commands." + className);

            Field commandNameField = c.getDeclaredField("commandName");
            Field syntaxField = c.getDeclaredField("syntax");
            whichListToAdd.add("**" + commandNameField.get("") + ":** `" + syntaxField.get("") + "`" + "\n");

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Help(){
        if (MODERATION_COMMANDS.isEmpty()){
            for (String className: MODERATION_COMMANDS_RAW){
                getHelpInfoAndAddToList(className, MODERATION_COMMANDS);
            }

            for (String className: MUSIC_COMMANDS_RAW){
                getHelpInfoAndAddToList(className, MUSIC_COMMANDS);
            }
        }

    }

    public void help(MessageReceivedEvent event){


        GenericPaginator mainPaginator = new GenericPaginator("Help command");
        mainPaginator.setChannel(event.getChannel());
        mainPaginator.setUserRequestedThis(event.getMember().getUser());
        mainPaginator.setUseDefaultEmotes(false);
        mainPaginator.setColor(Color.WHITE);

        mainPaginator.addEntry(new PaginatorEntry(helpTextBuilder.toString()));



        mainPaginator.addReaction(new PaginatorReaction("\uD83D\uDEE1️", ((member, emote, paginator, message) -> {
            if (!member.getId().equals(paginator.getUserRequestedThis().getId())) return;

            GenericPaginator moderationPaginator = new GenericPaginator("Moderation commands");
            moderationPaginator.setChannel(event.getChannel());
            moderationPaginator.setUserRequestedThis(member);
            moderationPaginator.setColor(Color.WHITE);
            moderationPaginator.setPaginatorMessage(message);
            moderationPaginator.setMaxElementsPerPage(10);

            for (String entry: MODERATION_COMMANDS){
                moderationPaginator.addEntry(new PaginatorEntry(entry));
            }

            moderationPaginator.sortEntries();

            moderationPaginator.addReaction(new PaginatorReaction("\uD83D\uDD19", (member1, emote1, paginator1, message1) -> {
                if (!member1.getId().equals(paginator1.getUserRequestedThis().getId())) return;

                PaginatorEventListener.getInstance().removePaginator(paginator1);

                moderationPaginator.clearReactions();

                mainPaginator.applyReactions();
                mainPaginator.send();
            }));


            mainPaginator.clearReactions();
            moderationPaginator.addDefaultReactions();
            moderationPaginator.applyReactions();

            moderationPaginator.send();
        })));

        mainPaginator.addReaction(new PaginatorReaction("\uD83C\uDFB5", ((member, emote, paginator, message) -> {
            if (!member.getId().equals(paginator.getUserRequestedThis().getId())) return;


            GenericPaginator musicPaginator = new GenericPaginator("Music commands");
            musicPaginator.setChannel(event.getChannel());
            musicPaginator.setUserRequestedThis(member);
            musicPaginator.setColor(Color.WHITE);
            musicPaginator.setPaginatorMessage(message);
            musicPaginator.setMaxElementsPerPage(10);

            for (String entry: MUSIC_COMMANDS){
                musicPaginator.addEntry(new PaginatorEntry(entry));
            }

            musicPaginator.sortEntries();

            musicPaginator.addEntry(new PaginatorEntry(""));

            musicPaginator.addEntry(new PaginatorEntry("Most of the music commands have been moved to use slash commands."));

            musicPaginator.addReaction(new PaginatorReaction("\uD83D\uDD19", (member1, emote1, paginator1, message1) -> {
                if (!member1.getId().equals(paginator1.getUserRequestedThis().getId())) return;

                PaginatorEventListener.getInstance().removePaginator(paginator1);

                musicPaginator.clearReactions();

                mainPaginator.applyReactions();
                mainPaginator.send();
            }));

            mainPaginator.clearReactions();
            musicPaginator.addDefaultReactions();
            musicPaginator.applyReactions();

            musicPaginator.send();
        })));

        mainPaginator.addReaction(new PaginatorReaction("❌", ((member, emote, paginator, message) -> {
            if (!member.getId().equals(paginator.getUserRequestedThis().getId())) return;

            paginator.close();
        })));

        mainPaginator.send();
    }

    public void execute(MessageReceivedEvent event, Args args) {

        if (args.isEmpty()){
            help(event);
            return;
        }

        Class c = null;

        // searches for the 'help' function in the class and invokes it

        try{

            // does a bit of mapping to get the right class name
            c = Constants.getClassNameFromCommandName(args.get(0));

            if (c == null){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "You are trying to get help for a command that doesn't exist!", Color.RED).build()).queue();
                return;
            }


            Field commandNameField, syntaxField, descriptionField;

            commandNameField = c.getDeclaredField("commandName");
            syntaxField = c.getDeclaredField("syntax");
            descriptionField = c.getDeclaredField("description");

            String commandName = (String) commandNameField.get("");
            String syntax = (String) syntaxField.get("");
            String description = (String) descriptionField.get("");

            Help.send(commandName, syntax, description, event);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
