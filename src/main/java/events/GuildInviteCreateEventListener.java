package events;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import utils.Constants;

import utils.Embed;

import java.awt.*;
import java.util.Objects;

public class GuildInviteCreateEventListener extends ListenerAdapter {

    // basically self explanatory

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event){
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel == null) return;

        String inviteUrl = event.getUrl();
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();

        AuditLogEntry firstEntry = null;

        for (AuditLogEntry entry : logs){
            if (entry.getType().equals(ActionType.INVITE_CREATE)){
                firstEntry = entry;
                break;
            }
        }

        if (firstEntry == null) return;

        if (Objects.equals(firstEntry.getUser(), event.getJDA().getSelfUser())) return;

        loggingChannel.sendMessageEmbeds(new Embed("Invite created", firstEntry.getUser().getAsMention() + " has created an invite!\n\nUrl: " + inviteUrl, Color.BLACK).build()).queue();
    }
}
