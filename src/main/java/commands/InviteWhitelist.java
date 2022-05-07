package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.stuffs.JsonStuff;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class InviteWhitelist extends CommandManager {

    public final static String commandName = "Invitewhitelist", syntax = "invitewhitelist (add / remove / resolve / list) [inviteUrl]", description = "Lets you create an invite-whitelist. Non whitelisted invites will be deleted! You can clear all whitelisted invites with `invitewhitelist remove all`!";


    private static final Permission permission = Permission.ADMINISTRATOR;

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permission)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the '" + permission.toString() + "' permission!", Color.RED).build()
            ).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an option!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        String option = argsList.get(0);

        switch (option){

            case "add":
                if (argsList.size() < 2){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provde an invite url or a server id to add to the whitelist!", Color.RED).build()).queue();
                    return;
                }
                List<Long> invites = JsonStuff.getLongListFromJson(Constants.getInviteWhitelistPath(), event.getGuild().getId());
                if (invites == null){
                    JsonStuff.writeLongListToJsonFile(Constants.getInviteWhitelistPath(), event.getGuild().getId(), new ArrayList<Long>());
                    invites = JsonStuff.getLongListFromJson(Constants.getInviteWhitelistPath(), event.getGuild().getId());
                }

                String firstInvite;
                try {
                    firstInvite = event.getMessage().getInvites().get(0);
                } catch (IndexOutOfBoundsException noted){
                    try{
                        Long serverId = Long.parseLong(argsList.get(1));
                        if (!invites.contains(serverId))
                            invites.add(serverId);
                        else {
                            event.getChannel().sendMessageEmbeds(new Embed("Error", "That server is already whitelisted!", Color.RED).build()).queue();
                            return;
                        }
                        JsonStuff.writeLongListToJsonFile(Constants.getInviteWhitelistPath(), event.getGuild().getId(), invites);
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "Invites from the server with id `" + serverId + "` are now whitelisted!", Color.GREEN).build()).queue();
                        return;
                    } catch (NumberFormatException noted2){
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "No invite or valid server id in arguments!", Color.RED).build()).queue();
                        return;
                    }
                }

                List<Long> finalInvites = invites;
                Invite.resolve(event.getJDA(), firstInvite).queue(v -> {
                    finalInvites.add(v.getGuild().getIdLong());
                    event.getChannel().sendMessageEmbeds(new Embed("Whitelisted server", "Invites from the server `" + v.getGuild().getName() + "` are now whitelisted!", Color.GREEN).build()).queue();
                    JsonStuff.writeLongListToJsonFile(Constants.getInviteWhitelistPath(), event.getGuild().getId(), finalInvites);
                }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_INVITE, e -> {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid invite!", Color.RED).build()).queue();
                }));
                break;


            case "remove":
                if (argsList.size() < 2){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provde an invite url or a server id to remove from the whitelist!", Color.RED).build()).queue();
                    return;
                }
                List<Long> invites2 = JsonStuff.getLongListFromJson(Constants.getInviteWhitelistPath(), event.getGuild().getId());
                if (invites2 == null || invites2.isEmpty()){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You have no whitelisted invites!", Color.RED).build()).queue();
                    return;
                }

                String firstInvite2;
                try {
                    firstInvite2 = event.getMessage().getInvites().get(0);
                } catch (IndexOutOfBoundsException noted){
                    if (argsList.get(1).equals("all")){
                        invites2.clear();
                        event.getChannel().sendMessageEmbeds(new Embed("Whitelisted server", "Successfully deleted all invite-whitelists!", Color.GREEN).build()).queue();
                        JsonStuff.writeLongListToJsonFile(Constants.getInviteWhitelistPath(), event.getGuild().getId(), invites2);
                        return;
                    }
                    try{
                        Long serverId = Long.parseLong(argsList.get(1));
                        if (invites2.contains(serverId)){
                            invites2.remove(serverId);
                            event.getChannel().sendMessageEmbeds(new Embed("Whitelisted server", "The server with id `" + serverId + "` is now blacklisted again!", Color.GREEN).build()).queue();
                            JsonStuff.writeLongListToJsonFile(Constants.getInviteWhitelistPath(), event.getGuild().getId(), invites2);
                            return;
                        } else {
                            event.getChannel().sendMessageEmbeds(new Embed("Error", "That server isn't whitelisted!", Color.RED).build()).queue();
                            return;
                        }
                    } catch (NumberFormatException noted2){
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "No invite or valid server id in arguments!", Color.RED).build()).queue();
                    }
                    return;
                }

                Invite.resolve(event.getJDA(), firstInvite2).queue(v -> {
                    if (invites2.contains(v.getGuild().getIdLong())){
                        invites2.remove(v.getGuild().getIdLong());
                        event.getChannel().sendMessageEmbeds(new Embed("Whitelisted server", "Invites from the server `" + v.getGuild().getName() + "` are now blacklisted again!", Color.GREEN).build()).queue();
                        JsonStuff.writeLongListToJsonFile(Constants.getInviteWhitelistPath(), event.getGuild().getId(), invites2);
                    }
                }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_INVITE, e -> {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid invite!", Color.RED).build()).queue();
                }));
                break;

            case "list":
                List<Long> invites3 = JsonStuff.getLongListFromJson(Constants.getInviteWhitelistPath(), event.getGuild().getId());
                if (invites3 == null || invites3.isEmpty()){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You have no whitelisted invites!", Color.RED).build()).queue();
                    return;
                }
                List<String> stuff = new ArrayList<String>(){{
                    for (Long guildId: invites3){
                        Guild guild = event.getJDA().getGuildById(guildId);
                        if (guild == null){
                            add(String.valueOf(guildId));
                        } else {
                            add(guild.getName());
                        }
                    }
                }};

                String message = "Following servers are whitelisted (if the bot is not on that server, the id will be shown):\n\n```" + String.join(", ", stuff) + "```";
                event.getChannel().sendMessageEmbeds(new Embed("Whitelisted servers", message, Color.GREEN).build()).queue();
                break;

            case "resolve":
                if (argsList.size() < 2){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an invite!", Color.RED).build()).queue();
                    return;
                }

                String firstInvite3;
                try {
                    firstInvite3 = event.getMessage().getInvites().get(0);
                } catch (IndexOutOfBoundsException noted){
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an invite in your arguments!", Color.RED).build()).queue();
                    return;
                }
                Invite.resolve(event.getJDA(), firstInvite3).queue(v -> {
                    String message2 =
                            "Guild name: `" + v.getGuild().getName() + "`,\n" +
                            "Guild id: `" + v.getGuild().getId() + "`,\n";

                    if (v.getInviter() != null){
                        message2 += "Invited created by: `" + v.getInviter().getName() + "`, ID: `" + v.getInviter().getId()  + "`,\n";
                    }
                    if (v.getGuild().getOnlineCount() != -1){
                        message2 += "Online Members: `" + v.getGuild().getOnlineCount() + "`,\n";
                    }
                    if (v.getGuild().getMemberCount() != -1){
                        message2 += "Member count: `" + v.getGuild().getMemberCount() + "`,\n";
                    }
                            // OnLy FoR eXpAnDeD iNvItEs
                            // "Uses: `" + v.getUses() + "/" + v.getMaxUses() + "`\n" +
                            // "Created at: `" + v.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")) + "`\n" +
                            // "Temporary: " + v.isTemporary();

                    event.getChannel().sendMessageEmbeds(new Embed("Invite information", message2, Color.GREEN).build()).queue();
                }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_INVITE, e -> {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid invite!", Color.GREEN).build()).queue();
                }));
                break;

            default:
                event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid option!", Color.RED).build()).queue();
                break;
        }
    }
}
