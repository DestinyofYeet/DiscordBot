package commands;

import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;

public class PlayNext extends CommandManager {

    public static final String commandName = "play_next", syntax = "play_next (link or keyword)", description = "Will append the song at the top of the queue, not at the end!";

    public void execute(MessageReceivedEvent event, Args args){
        Play play = new Play();
        play.execute(event, args, true);
    }
}
