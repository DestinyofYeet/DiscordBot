package events;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import utils.*;
import utils.stuffs.JsonStuff;
import utils.stuffs.MsgCacheStuff;
import utils.stuffs.PrefixStuff;


import java.awt.Color;
import java.util.List;

public class MessageReceivedEventListener extends ListenerAdapter {
    // listens for a new message

    @Override
    public void onMessageReceived (@NotNull MessageReceivedEvent event){
        if (!event.isFromGuild()) return;

        MsgCacheStuff.writeToCache(event);

        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());

        if (event.getMessage().getContentRaw().equals("!help") && !PrefixStuff.getPrefix(event.getGuild().getIdLong()).equalsIgnoreCase("!")){
            event.getChannel().sendMessage("Type " + PrefixStuff.getPrefix(event.getGuild().getIdLong()) + "help for more help").queue();
        }
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("```Timeout for") && content.endsWith("has finished!```") && event.getMember().getUser().equals(event.getJDA().getSelfUser())){
            String memberId = content.split("\\(")[1].split("\\)")[0];
            Member member = Constants.getMemberById(event.getGuild(), memberId);
            if (member == null) return;
            member.mute(false).queue();
        }
        List<Long> invites = JsonStuff.getLongListFromJson(Constants.getInviteWhitelistPath(), event.getGuild().getId());
        if (invites != null && invites.size() > 0){
            for (String invite: event.getMessage().getInvites()){
                Invite.resolve(event.getJDA(), invite).queue(v -> {
                    if (!invites.contains(v.getGuild().getIdLong())){
                        event.getMessage().delete().queue();
                        if (loggingChannel != null){
                            loggingChannel.sendMessageEmbeds(new Embed("Non-whitelisted url", event.getMember().getAsMention() + " has sent an non-whitelisted url! The message has been deleted and can be seen below!\n\n URL: " + v.getUrl(), Color.BLACK).build()).queue();
                        }
                    }
                }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_INVITE));
            }
        }
    }
}
