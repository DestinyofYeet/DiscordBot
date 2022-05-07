package main;


import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;
import utils.*;
import utils.stuffs.PrefixStuff;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CommandManager extends ListenerAdapter {
    private final Logger logger;

    public CommandManager(){
        this.logger = new Logger("Command Manager");
    }

    // "main" listener event
    private static Boolean botIsLocked = false;

    HashMap<String, String> classStringMap = Constants.getClassStringMap();

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getChannelType().equals(ChannelType.PRIVATE) || event.getChannelType().equals(ChannelType.GROUP) || !event.isFromGuild()){
            return;
        }

        try {
            String prefix = PrefixStuff.getPrefix(event.getGuild().getIdLong());

            if (!event.getMessage().getContentRaw().startsWith(prefix)) return;
            String commandName = event.getMessage().getContentRaw().split(prefix)[1].split(" ")[0];

            if (!commandName.equals("botlock") && botIsLocked){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "The bot is currently locked! Please try again in a few minutes!", Color.RED).build()).queue();
                return;
            }
//            event.getChannel().sendTyping().queue();
            Args args = new Args(event.getMessage().getContentRaw());

            Class c = null;

            try {

                c = Constants.getClassNameFromCommandName(commandName);

                if (c == null) {
                    // invalid command
                    return;
                }

                Method execute = c.getDeclaredMethod("execute", MessageReceivedEvent.class, Args.class);
                Object o = c.getDeclaredConstructor().newInstance();

                logger.info("Executing command: " + commandName);

                Thread thread = new Thread(() -> {
                    try {
                        execute.invoke(o, event, args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();

                        String filename = "data/" + event.getGuild().getId() + ".err.txt";

                        try {
                            e.printStackTrace(new PrintStream(filename));
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }

                        event.getChannel().sendMessageEmbeds(new Embed("Fatal Error", "Something failed: " + e.getMessage(), Color.RED).build()).queue();

                        event.getChannel().sendFile(new File(filename), "Traceback.txt").queue();

                        try {
                            Files.deleteIfExists(Path.of(filename));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    logger.info("Done executing command: " + commandName);
                });

                thread.start();

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();

                String filename = "data/" + event.getGuild().getId() + ".err.txt";

                try {
                    e.printStackTrace(new PrintStream(filename));
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }

                event.getChannel().sendMessageEmbeds(new Embed("Fatal Error", "Something failed: " + e.getMessage(), Color.RED).build()).queue();

                event.getChannel().sendFile(new File(filename), "Traceback.txt").queue();

                Files.deleteIfExists(Path.of(filename));
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {

        } catch (HierarchyException e){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You are trying to interact with somebody that has a higher role than the bot has!", Color.RED).build()).queue();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void setBotIsLocked(boolean enabled){
        botIsLocked = enabled;
    }

    public static Boolean getBotIsLocked() {
        return botIsLocked;
    }

    public java.util.List<String> getCommands(){
        // gets all class names that extend from the CommandManager.java class
        Reflections reflections = new Reflections("commands");

        Set<Class<? extends CommandManager>> classes = reflections.getSubTypesOf(CommandManager.class);
        HashMap<String, String> classStringMap = Constants.getClassStringMap();

        return new ArrayList<String>(){{
            for (Class<? extends CommandManager> aClass: classes){

                String className = aClass.getSimpleName();

                if (classStringMap.containsValue(className)){
                    for (String key: classStringMap.keySet()){
                        if (classStringMap.get(key).equals(className)){
                            add(key);
                        }
                    }

                } else {
                    add(className.toLowerCase());
                }
            }

        }};
    }
}
