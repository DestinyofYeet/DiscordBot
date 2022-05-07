package commands;

import main.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;

import utils.Embed;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInfo extends CommandManager {

    public static final String commandName = "Userinfo", syntax = "userinfo (userId)", description = "Lets you see some stats about that user!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessageEmbeds(new Embed("Insufficient permissions!", "You need the 'Administrator' permission to use this command!", Color.RED).build()).queue();
            return;
        }

        Member member = null;
        if (!args.isEmpty()){
            member = Constants.getMemberById(event.getGuild(), args.get(0));
        }

        if (member == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Member not found!", Color.RED).build()).queue();
            event.getChannel().sendTyping();
            return;
        }

        Role topRole = null;

        List<String> roles = new ArrayList<String>();

        for (Role role : member.getRoles()){
            roles.add(role.getAsMention());
            if (topRole == null){
                topRole = role;

            } else {
                if (topRole.getPosition() < role.getPosition()){
                    topRole = role;
                }
            }
        }

        String nickName = member.getNickname();
        if (nickName == null){
            nickName = "None";
        }

        String topRoleString = null;
        if (topRole == null){
            topRoleString = "None";

        } else {
            topRoleString = topRole.getAsMention();
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setAuthor("User Info - " + member.getEffectiveName() + " - " + member.getId());
        embed.setThumbnail(member.getUser().getAvatarUrl());
        embed.setFooter("Requested by " + event.getMember().getUser().getAsTag() + " | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")), event.getAuthor().getAvatarUrl());
        embed.addField("Nickname:", nickName, false);
        embed.addField("Created at:", member.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")), true);
        embed.addField("Joined at:", member.getTimeJoined().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")), false);
        embed.addField("Roles (" + roles.size() + "):", String.join(",\n", roles), false);
        embed.addField("Top role:", topRoleString, false);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
