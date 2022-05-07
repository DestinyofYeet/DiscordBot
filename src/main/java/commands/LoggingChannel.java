package commands;

import main.CommandManager;
import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.*;
import utils.sql.SQLRequest;
import utils.sql.RequestType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LoggingChannel extends CommandManager {

    public static final String commandName = "logging_channel", syntax = "logging_channel (set / remove) (channelId)", description = "Lets you set a logging channel for this server!";

    public void execute(MessageReceivedEvent event, Args args) {
        Member member = event.getMember();

        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()) {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide arguments!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        String specifiedOption = argsList.get(0);

        String sql = null;

        ArrayList<String> data = null;

        switch (specifiedOption) {
            case "remove" -> {
                sql = "drop from table loggingChannels where guildID=?";
                data = new ArrayList<>() {{
                    add(event.getGuild().getId());
                }};
                event.getChannel().sendMessageEmbeds(new Embed("Success", "Successfully removed logging channel", Color.GREEN).build()).queue();
            }
            case "set" -> {
                if (argsList.size() < 2) {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a channel!", Color.RED).build()).queue();
                    return;
                }
                TextChannel channel = Constants.getTextChannel(event.getGuild(), argsList.get(1));
                if (channel == null) {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "That channel doesn't exist!", Color.RED).build()).queue();
                    return;
                }
                Constants.setLoggingChannel(channel.getGuild(), channel.getId());
                event.getChannel().sendMessageEmbeds(new Embed("Success", "Successfully set logging channel to " + channel.getAsMention() + "!", Color.GREEN).build()).queue();
            }
            default -> event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid option!", Color.RED).build()).queue();
        }

        if (sql != null){
            Main.getRequestManager().queue(new SQLRequest(RequestType.EXECUTE, sql, data));
        }
    }
}
