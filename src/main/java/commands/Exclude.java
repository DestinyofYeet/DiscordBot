package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.*;
import utils.stuffs.JsonStuff;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Exclude extends CommandManager {
    // makes the bot ignore the channel

    public static final String commandName = "Exclude", syntax = "exclude (add / remove / list) (channelId)", description = "Lets you exclude a channel from all editing or deleting";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permission! You need the 'Administrator' permission!", Color.RED).build()).queue();
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an argument!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        String option = argsList.get(0);

        if (argsList.size() < 2 && !option.equals("list")){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a channel!", Color.RED).build()).queue();
            return;
        }

        List<Long> allExcludedChannels = JsonStuff.getLongListFromJson(Constants.getExcludedChannelsPath(), event.getGuild().getId());
        if (allExcludedChannels == null){
            allExcludedChannels = new ArrayList<Long>();
        }

        if (option.equalsIgnoreCase("list")) {
            List<Long> finalAllExcludedChannels = allExcludedChannels;
            List<String> allExcludedChannelsString = new ArrayList<String>() {{
                for (Long currentChannelId: finalAllExcludedChannels) {
                    add(event.getGuild().getTextChannelById(currentChannelId).getAsMention());
                }
            }};
            event.getChannel().sendMessageEmbeds(new Embed("Excluded channels", "The following channels are excluded:\n\n" + String.join(", ", allExcludedChannelsString), Color.GREEN).build()).queue();
            return;
        }
        TextChannel channelToExclude = Constants.getTextChannel(event.getGuild(), argsList.get(1));

        if (channelToExclude == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That channel doesn't exist!", Color.RED).build()).queue();
            return;
        }

        if (option.equalsIgnoreCase("add")){
            if (!allExcludedChannels.contains(channelToExclude.getIdLong())) {
                allExcludedChannels.add(channelToExclude.getIdLong());
                event.getChannel().sendMessageEmbeds(new Embed("Excluded", "The channel " + channelToExclude.getAsMention() + " is now excluded!", Color.GREEN).build()).queue();
            } else
                event.getChannel().sendMessageEmbeds(new Embed("Error", "The channel " + channelToExclude.getAsMention() + " is already excluded!", Color.RED).build()).queue();

        } else if (option.equalsIgnoreCase("remove")){
            if (allExcludedChannels.contains(channelToExclude.getIdLong())) {
                allExcludedChannels.remove(channelToExclude.getIdLong());
                event.getChannel().sendMessageEmbeds(new Embed("Included", "The channel " + channelToExclude.getAsMention() + " is now included in moderation again!", Color.GREEN).build()).queue();

            } else
                event.getChannel().sendMessageEmbeds(new Embed("Error", "The channel" + channelToExclude.getAsMention() + " was never excluded!", Color.RED).build()).queue();

        } else {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid editing option!", Color.RED).build()).queue();
            return;
        }

        JsonStuff.writeLongListToJsonFile(Constants.getExcludedChannelsPath(), event.getGuild().getId(), allExcludedChannels);
    }
}
