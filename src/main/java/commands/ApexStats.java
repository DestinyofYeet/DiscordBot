package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Embed;
import utils.gamestats.apexlegends.GetPlayerStatsByNameRequest;
import utils.gamestats.apexlegends.generics.statistics.ApexPlayer;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class ApexStats {

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

        ApexPlayer player = statsRequest.getApexPlayer();

        if (player == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Player " + playerName + " not found!", Color.RED).build()).queue();
            return;
        }

        Embed embed = new Embed("Statistics for player: " + player.getName(), "", Color.GREEN);

        embed.addField("Level:", player.getLevel() + " | " + player.getToNextLevelPercent() + "%", true);

        embed.addField("Selected Legend:", player.getSelectedLegend().getName(), true);

        embed.addField("Platform", player.getPlatform(), true);

//        StringBuilder badgeString = new StringBuilder();
//
//        for (Badge badge : player.getSelectedLegend().getBadges()){
//            badgeString.append(badge.getName()).append(badge.getValue() > 0 ? ": `" + badge.getValue() : "`");
//            badgeString.append("\n");
//        }
//
//
//        embed.addField("Badges:", badgeString.toString());

        embed.addField("BR rank:", "Season: `" + player.getBrRanked().getRankedSeason() + "`\n" +
                "Rank: `" + player.getBrRanked().getRankName() + " " + player.getBrRanked().getRankDiv() + "`\n" +
                "RP: `" + player.getBrRanked().getRankScore() + "`", true);

        embed.addField("Arena rank:", "Season: `" + player.getArenaRanked().getRankedSeason() + "`\n" +
                "Rank: `" + player.getArenaRanked().getRankName() + " " + player.getArenaRanked().getRankDiv() + "`\n" +
                "RP: `" + player.getArenaRanked().getRankScore() + "`", true);

        embed.setThumbnail(player.getSelectedLegend().getIconURL());

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
