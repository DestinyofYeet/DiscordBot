package commands;

import main.CommandManager;
import utils.Args;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.temporal.ChronoUnit;

public class Ping extends CommandManager {

    public final static String commandName = "Ping", syntax = "ping", description = "Lets you see some latency information!";

    public void execute(MessageReceivedEvent event, Args args){
        event.getMessage().reply("Ping...").queue(m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
            }
        );
    }
}
