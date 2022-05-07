package commands;

import io.opencensus.trace.Link;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.verificationLevel.VerificationLevel;
import utils.verificationLevel.VerificationLevelStuff;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class VerificationLevelCommand {

    public static final String commandName = "Set Verification Level", syntax = "verification_level [set] [level / verification_role / verification_message] [value]", description = "Lets you set different verification levels for your server!\n0 = No verification (default),\n1 = check a reaction\n2 = solve a captcha (distorted image)";

    private final Permission PERMISSION_NEEDED = Permission.ADMINISTRATOR;

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(PERMISSION_NEEDED)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the " + PERMISSION_NEEDED + " permission!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            utils.verificationLevel.VerificationLevel level = VerificationLevelStuff.getVerificationLevel(event.getGuild().getId());

            if (level == null){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "Failed to check current verification setting!", Color.RED).build()).queue();
                return;
            }

            event.getChannel().sendMessageEmbeds(new Embed("Set Verification Level", "You have following verification setting set: `" + level.getLevel() + " = " + (level.getLevel() == 0 ? "No verification": Constants.capitalizeString(level.toString())) + "` ", Color.GREEN).build()).queue();
            return;
        }

        if (args.get(0).equalsIgnoreCase("set")) {
            if (args.size() < 2) {
                event.getChannel().sendMessageEmbeds(new Embed("Error", "You did not provide an argument on what to set!", Color.RED).build()).queue();
                return;
            }

            switch (args.get(1).toLowerCase()) {
                case "level" -> {
                    if (args.size() < 3) {
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "You did not provide a new level to set the verification level to!", Color.RED).build()).queue();
                        return;
                    }

                    String levelString = args.get(2);

                    int levelInt;

                    try {
                        levelInt = Integer.parseInt(levelString);
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "Input is not a number!", Color.RED).build()).queue();
                        return;
                    }

                    VerificationLevel level = utils.verificationLevel.VerificationLevel.getFromLevel(levelInt);

                    if (level == null) {
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid level: " + levelInt, Color.RED).build()).queue();
                        return;
                    }

                    if (VerificationLevelStuff.getVerificationLevel(event.getGuild().getId()) == level) {
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "The verification level is already at that level!", Color.RED).build()).queue();
                        return;
                    }

                    VerificationLevelStuff.setVerificationLevel(event.getGuild().getId(), level);

                    if (level.getLevel() > 0) {
                        String existingCategoryId = VerificationLevelStuff.getVerificationCategory(event.getGuild().getId());

                        String existingRoleId = VerificationLevelStuff.getVerificationRoleId(event.getGuild().getId());

                        Role verificationRole = null;
                        Category verificationCategory = null;

                        boolean createNewCategory = false;
                        boolean createNewRole = false;

                        if (existingCategoryId == null) {
                            createNewCategory = true;
                        } else {
                            verificationCategory = event.getGuild().getCategoryById(existingCategoryId);
                            if (verificationCategory == null) {
                                createNewCategory = true;
                            }
                        }

                        if (existingRoleId == null) {
                            createNewRole = true;
                        } else {
                            verificationRole = event.getGuild().getRoleById(existingRoleId);
                            if (verificationRole == null) {
                                createNewRole = true;
                            }
                        }

                        if (createNewCategory) {
                            verificationCategory = event.getGuild().createCategory("Verification").complete();
                            event.getGuild().modifyCategoryPositions().selectPosition(verificationCategory).moveTo(0).queue();
                            VerificationLevelStuff.setVerificationCategory(event.getGuild().getId(), verificationCategory.getId());
                        }


                        if (createNewRole) {
                            verificationRole = event.getGuild().createRole().setName("Verified").complete();
                            VerificationLevelStuff.setVerificationRoleId(event.getGuild().getId(), verificationRole.getId());
                        }

                        Role everyone = event.getGuild().getPublicRole();

                        LinkedList<String> listOfLockedChannels = new LinkedList<>();

                        for (GuildChannel channel : event.getGuild().getChannels()) {

                            PermissionOverride override = channel.getPermissionContainer().getPermissionOverride(everyone);

                            if (override != null){
                                if (override.getDenied().contains(Permission.VIEW_CHANNEL)){
                                    listOfLockedChannels.add(channel.getId());
                                }
                            }

                            if (!listOfLockedChannels.contains(channel.getId())){
                                channel.getPermissionContainer().putPermissionOverride(everyone)
                                        .setDeny(Permission.VIEW_CHANNEL).queue();

                                channel.getPermissionContainer().putPermissionOverride(verificationRole)
                                        .setAllow(Permission.VIEW_CHANNEL).queue();
                            }

                        }

                        VerificationLevelStuff.setWasLockedBefore(event.getGuild().getId(), listOfLockedChannels);

                        for (Member member : event.getGuild().getMembers()) {
                            if (!member.getRoles().contains(verificationRole))
                                event.getGuild().addRoleToMember(member, verificationRole).queue();
                        }

                        verificationCategory.getPermissionContainer().putPermissionOverride(everyone)
                                        .setAllow(Permission.VIEW_CHANNEL).queue();

                        verificationCategory.getPermissionContainer().putPermissionOverride(verificationRole)
                                        .setDeny(Permission.VIEW_CHANNEL).queue();

                        event.getChannel().sendMessageEmbeds(new Embed("Verification Level", "Successfully set verification level to " + level.getLevel() + "!", Color.GREEN).build()).queue();

                        MessageChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());

                        if (loggingChannel != null) {
                            loggingChannel.sendMessageEmbeds(new Embed("Verification Level", event.getMember().getAsMention() + " has set the verification level to " + level.getLevel() + "!", Color.BLACK).build()).queue();
                        }

                    } else {
                        Role everyone = event.getGuild().getPublicRole();

                        LinkedList<String> lockedChannels = VerificationLevelStuff.getWasLockedBefore(event.getGuild().getId());

                        for (GuildChannel channel : event.getGuild().getChannels()) {
                            if (!lockedChannels.contains(channel.getId())){
                                channel.getPermissionContainer().putPermissionOverride(everyone)
                                        .setAllow(Permission.VIEW_CHANNEL).queue();
                            }
                        }

                        event.getChannel().sendMessageEmbeds(new Embed("Verification Level", "Successfully set verification level to " + level.getLevel() + "!", Color.GREEN).build()).queue();

                        MessageChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());

                        if (loggingChannel != null) {
                            loggingChannel.sendMessageEmbeds(new Embed("Verification Level", event.getMember().getAsMention() + " has set the verification level to " + level.getLevel() + "!", Color.BLACK).build()).queue();
                        }


                    }

                }

                case "verification_role" -> {
                    if (args.size() < 3) {
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "You did not provide a new role id to set the verification role to!", Color.RED).build()).queue();
                        return;
                    }

                    Role role = Constants.getRole(event.getGuild(), args.get(2));

                    if (role == null) {
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a valid role!", Color.RED).build()).queue();
                        return;
                    }

                    VerificationLevelStuff.setVerificationRoleId(event.getGuild().getId(), role.getId());
                }

                case "verification_message" -> {
                    if (args.size() < 3){
                        event.getChannel().sendMessageEmbeds(new Embed("Error", "You did not provide a verification text!", Color.RED).build()).queue();
                        return;
                    }

                    args.getArgs().subList(0, 2).clear();

                    String text = String.join(" ", args.getArgs());

                    VerificationLevelStuff.setVerificationText(event.getGuild().getId(), text);

                    event.getChannel().sendMessageEmbeds(new Embed("Verification Level", "Successfully set this text as the verification text:\n```" + text + "```", Color.GREEN).build()).queue();
                    return;
                }

                default -> {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid arguments!", Color.RED).build()).queue();
                }
            }

        }

    }
}
