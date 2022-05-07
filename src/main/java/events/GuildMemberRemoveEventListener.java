package events;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.Constants;

import utils.Embed;

import java.awt.*;
import java.time.LocalDateTime;

public class GuildMemberRemoveEventListener extends ListenerAdapter {
    // sends in the message like "user removed user" or "user left" or "user banned user for reason"

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        int localSecond = LocalDateTime.now().getSecond();
        int localMinute = LocalDateTime.now().getMinute();
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel == null) return;

        AuditLogEntry lastBanEntry = null;
        AuditLogEntry lastKickEntry = null;
        int counter = 0;

        for (AuditLogEntry currentEntry: event.getGuild().retrieveAuditLogs()){
            if (counter != 0) break;

            if (currentEntry.getType().equals(ActionType.BAN))
                lastBanEntry = currentEntry;
            counter++;
        }
        counter = 0;

        for (AuditLogEntry currentEntry: event.getGuild().retrieveAuditLogs()){
            if (counter != 0) break;

            if (currentEntry.getType().equals(ActionType.KICK))
                lastKickEntry = currentEntry;
            counter++;
        }

        if (lastKickEntry != null){
            boolean timeCheck = false;
            int lastKickEntrySecond = lastKickEntry.getTimeCreated().getSecond();
            int lastKickEntryMinute = lastKickEntry.getTimeCreated().getMinute();
            if (lastKickEntrySecond <= localSecond + 1 && lastKickEntryMinute == localMinute) timeCheck = true;
            if (timeCheck) {
                if (lastKickEntry.getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return;
                String reason = lastKickEntry.getReason();
                if (reason != null)
                    loggingChannel.sendMessageEmbeds(new Embed("Kick", lastKickEntry.getUser().getAsMention() + " kicked " + event.getUser().getName() + " with id " + event.getUser().getIdLong() + " for `" + reason + "`!", Color.BLACK).build()).queue();
                else
                    loggingChannel.sendMessageEmbeds(new Embed("Kick", lastKickEntry.getUser().getAsMention() + " kicked " + event.getUser().getName() + " with id " + event.getUser().getIdLong() + "!", Color.BLACK).build()).queue();
                return;
            }
        }

        if (lastBanEntry != null){
            boolean timeCheck = false;
            int lastBanEntrySecond = lastBanEntry.getTimeCreated().getSecond();
            int lastBanEntryMinute = lastBanEntry.getTimeCreated().getMinute();
            if (lastBanEntrySecond <= localSecond + 1 && lastBanEntryMinute == localMinute) timeCheck = true;
            if (timeCheck) {
                if (lastBanEntry.getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return;
                String reason = lastBanEntry.getReason();
                if (reason != null)
                    loggingChannel.sendMessageEmbeds(new Embed("Ban", lastBanEntry.getUser().getAsMention() + " banned " + event.getUser().getName() + " with id " + event.getUser().getIdLong() + " for `" + reason + "`!", Color.BLACK).build()).queue();
                else
                    loggingChannel.sendMessageEmbeds(new Embed("Ban", lastBanEntry.getUser().getAsMention() + " banned " + event.getUser().getName() + " with id " + event.getUser().getIdLong() + "!", Color.BLACK).build()).queue();
                return;
            }
        }

        loggingChannel.sendMessageEmbeds(new Embed("Left", event.getUser().getName() + " with id " + event.getUser().getId() + " left!", Color.BLACK).build()).queue();
    }
}
