package events;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.Embed;

import java.awt.*;
import java.util.Objects;

public class GuildInviteDeleteEventListener extends ListenerAdapter {

    @Override
    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event){
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel == null) return;

        String inviteUrl = event.getUrl();
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();

        AuditLogEntry firstEntry = null;

        for (AuditLogEntry entry : logs){
            if (entry.getType().equals(ActionType.INVITE_DELETE)){
                firstEntry = entry;
                break;
            }
        }

        if (firstEntry == null) return;

        if (Objects.equals(firstEntry.getUser(), event.getJDA().getSelfUser())) return;

        loggingChannel.sendMessageEmbeds(new Embed("Invite deleted", firstEntry.getUser().getAsMention() + " has deleted an invite!\n\nUrl: " + inviteUrl, Color.BLACK).build()).queue();
    }
}
