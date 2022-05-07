package commands;

import main.CommandManager;
import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.*;
import utils.sql.Request;
import utils.sql.RequestType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Setup extends CommandManager {

    public static final String commandName = "Setup", syntax = "setup [undo]", description = "Sets up server logging for you. Can be removed with the undo argument!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient Permissions! You need the 'Administrator' permission to use this command!", Color.RED).build()).queue();
            return;
        }

        Guild guild = event.getGuild();

        if (args.isEmpty()) {
            Category category = guild.createCategory("Kermit Moderation Bot").complete();
            TextChannel serverLogChannel = category.createTextChannel("server-log").complete();

            Constants.setLoggingChannel(guild, serverLogChannel.getId());

            event.getChannel().sendMessageEmbeds(new Embed("Setup", "Server logging has been set up!", Color.GREEN).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        String option = argsList.get(0);

        if ("undo".equals(option)) {
            List<Category> categories = event.getGuild().getCategoriesByName("Kermit Moderation Bot", false);
            for (Category category : categories) {
                for (GuildChannel channel : category.getChannels()) {
                    channel.delete().queue();
                }
                category.delete().queue();
            }
            event.getChannel().sendMessageEmbeds(new Embed("Setup", "Successfully reversed setup!", Color.GREEN).build()).queue();
        } else {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid option!", Color.RED).build()).queue();
        }
    }
}
