package slash_commands;

import main.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Args;

public class PlayNext extends CommandManager {

    public static final String commandName = "play_next", syntax = "play_next (link or keyword)", description = "Will append the song at the top of the queue, not at the end!";

    public final static SlashCommandData command = Commands.slash("play_next", "Will append the song at the top of the queue, not at the end!")
            .addOption(OptionType.STRING, "query", "The query to search for or the link!");

    public void execute(SlashCommandInteractionEvent event){
        Play play = new Play();
        play.execute(event, true);
    }
}
