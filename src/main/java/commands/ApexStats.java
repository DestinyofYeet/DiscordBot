package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Embed;
import utils.gamestats.apexlegends.GetPlayerStatsByNameRequest;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class ApexStats {
    // Still in progress

    public static final String commandName = "Apex Stats", syntax = "apex_stats (username) [platform]", description = "Lets you get stats about an apex legends player!";

    public void help(MessageReceivedEvent event){
        commands.Help.send(commandName, syntax,  description, event);
    }

    public void execute(MessageReceivedEvent event, Args args){
        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a name to search for!", Color.RED).build()).queue();
            return;
        }

        String playerName = args.get(0);

        String platform = "PC";

        if (args.size() > 1){
            String newPlatform = args.get(1).toUpperCase();

            if (!Arrays.asList("PS4", "X1", "PC").contains(newPlatform)){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid platform specified! Valid platforms are: \nPS4: Playstation 4 and 5\nX1: Xbox\nPC: Origin / Steam", Color.RED).build()).queue();
                return;
            }

            platform = newPlatform;
        }

        GetPlayerStatsByNameRequest statsRequest = new GetPlayerStatsByNameRequest(playerName, platform);

        try {
            statsRequest.doRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
