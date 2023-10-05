package slash_commands;

import main.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Constants;
import utils.Embed;

import java.awt.*;

public class Auth {

    public final static SlashCommandData command = Commands.slash("auth", "Authenticates to the uwuwhatsthis api");

    public void execute(SlashCommandInteractionEvent event){
        if (!Constants.getTrustedIds().contains(event.getInteraction().getMember().getUser().getIdLong())){
            event.replyEmbeds(new Embed("Error", "You are not authorized to run this command!", Color.RED).build()).setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        Main.getUwuwhatsthisApiManager().authorize();

        event.getHook().editOriginalEmbeds(new Embed("Auth", "Authorization queued.", Color.GREEN).build()).queue();
    }
}
