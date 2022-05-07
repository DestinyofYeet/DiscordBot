package commands;

import main.CommandManager;
import main.Main;
import utils.Args;
import utils.Constants;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Embed;

import java.awt.*;

public class Restart extends CommandManager {

    public final static String commandName = "Restart", syntax = "restart", description = "Lets you restart the bot. You need to be trusted to run this command!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!Constants.getTrustedIds().contains(event.getMember().getIdLong())){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You have to be trusted to use this command!", Color.RED).build()).queue();
            return;
        }

        Main.getRequestManager().stop();

        event.getChannel().sendMessageEmbeds(new Embed("Restart", "Restarting!", Color.RED).build()).queue();
        event.getJDA().getRegisteredListeners().forEach(event.getJDA()::removeEventListener);
        try{
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        try {
            event.getJDA().shutdownNow();
        } catch (Exception ignored){}
    }
}
