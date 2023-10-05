package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SlashCommandManager extends ListenerAdapter {

    private final Logger logger;

    private final HashMap<String, Class> commandClassMapCache;
    private final ArrayList<String> classList;

    public SlashCommandManager(){
        this.logger = new Logger("SlashCommandManager");
        this.commandClassMapCache = new HashMap<>();

        classList = new ArrayList<>(){{
            add("Ping");
            add("BanIp");
            add("Auth");
            add("Play");
            add("Join");
            add("Disconnect");
            add("PlayNext");
            add("Search");
            add("Pause");
            add("Queue");
            add("NowPlaying");
            add("Seek");
            add("Skip");
            add("Volume");
            add("ClearQ");
            add("Shuffle");
        }};

        buildMap();
    }

    private void buildMap(){
        final String packageName = "slash_commands";

        logger.info("Building cache map of commands in package '" + packageName + "'");

        for (String className: classList){
            try {
                Class c = Class.forName(packageName + "." + className);

                SlashCommandData data = (SlashCommandData) c.getDeclaredField("command").get("");

                String commandName = data.getName();

                commandClassMapCache.put(commandName, c);

                logger.info("Added " + className + " to cache");

            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }

        logger.info("Done building cache!");
    }

    public void upsertCommands(JDA jda, Guild guild){
        final int commandsAmount = commandClassMapCache.size();
        AtomicInteger commandsDone = new AtomicInteger();

        commandClassMapCache.forEach((commandName, classObject) -> {
            SlashCommandData data;

            int newCommandsDone = commandsDone.addAndGet(1);

            try {
                data = (SlashCommandData) classObject.getDeclaredField("command").get("");
            } catch (IllegalAccessException | NoSuchFieldException e) {
                logger.error("Could not get SlashCommandData from command '" + commandName + "'!");
                return;
            }

            if (guild == null){
                jda.upsertCommand(data).complete();
                logger.info("Upserted command: " + data.getName() + " to global scope (" + newCommandsDone + "/" + commandsAmount + ")");
            } else {
                guild.upsertCommand(data).complete();
                logger.info("Upserted command: " + data.getName() + " to guild scope " + guild.getName() + " (" + newCommandsDone + "/" + commandsAmount + ")");
            }
        });
    }

    public void upsertCommands(JDA jda){
        upsertCommands(jda, null);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        String eventName = event.getName();

        Class c = this.commandClassMapCache.get(eventName);

        if (c == null){
            logger.error("No class found for event name '" + eventName + "'");
            return;
        }

        try {
            Method execute = c.getDeclaredMethod("execute", SlashCommandInteractionEvent.class);
            Object o = c.getDeclaredConstructor().newInstance();

            execute.invoke(o, event);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

    }
}
