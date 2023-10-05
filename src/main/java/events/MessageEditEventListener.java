package events;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.*;
import utils.stuffs.JsonStuff;
import utils.stuffs.MsgCacheStuff;

import java.awt.*;
import java.util.List;

public class MessageEditEventListener extends ListenerAdapter {
    // listens for message edit events

    @Override
    public void onMessageUpdate (MessageUpdateEvent event){
        if (!event.getChannelType().equals(ChannelType.TEXT)) return;
        List<Long> channelsExcludedIds = JsonStuff.getLongListFromJson(Constants.getExcludedChannelsPath(), event.getGuild().getId());
        if (channelsExcludedIds != null)
            if (channelsExcludedIds.contains(event.getChannel().getIdLong())) return;

        Message message = event.getMessage();

        CachedMessage cachedMessage = MsgCacheStuff.readFromCache(event.getJDA(), event.getMessageId());
        String string = null;
        if (cachedMessage == null){
            string = "A message from got edited in " + event.getChannel().getAsMention() + ":\n\n**Message:**\n" + "```" + message.getContentRaw() + "```" + "\n\nThe bot doesn't have the original message chached, that's why you only see the edited one!";

        } else {
            String cachedMessageContent = cachedMessage.getContent();
            Member member = cachedMessage.getMember();
            if (member.getUser() == event.getJDA().getSelfUser()) return;
            if (cachedMessageContent != null){
                if (message.getContentRaw().length() == 0) return;
                if (message.getContentRaw().equals(cachedMessageContent)) return;
                if (cachedMessageContent.length() == 0) return;
                string = "A message from " + member.getAsMention() + " got edited in " + event.getChannel().getAsMention() + ":\n\n**Before:**\n" + "```" + cachedMessageContent + "```" + "\n\n**After:**\n" + "```"  + message.getContentRaw()+ "```";
            }
        }


        string = string + "\n\n[Click to jump to message](" + message.getJumpUrl() + ")";
        Constants.getLoggingChannel(event.getGuild()).sendMessageEmbeds(new Embed("Message got edited", string, Color.BLACK).build()).queue();
    }
}
