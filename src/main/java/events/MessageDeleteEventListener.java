package events;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.*;
import utils.stuffs.JsonStuff;
import utils.stuffs.MessageBlacklistStuff;
import utils.stuffs.MsgCacheStuff;
import utils.stuffs.PrefixStuff;

import java.awt.*;
import java.util.List;

public class MessageDeleteEventListener extends ListenerAdapter {
    // sends in the logging channel "A message got deleted from member in channel..."

    @Override
    public void onMessageDelete(MessageDeleteEvent event){
        if (!(event.getChannelType().equals(ChannelType.TEXT))) return;
        TextChannel actionChannel = event.getChannel().asTextChannel();
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel == null) return;
        if (actionChannel.getIdLong() == (Constants.getLoggingChannel(event.getGuild()).getIdLong())) return;
        List<Long> channelsExcludedIds = JsonStuff.getLongListFromJson(Constants.getExcludedChannelsPath(), event.getGuild().getId());
        if (channelsExcludedIds != null)
            if (channelsExcludedIds.contains(event.getChannel().getIdLong())) return;


        CachedMessage msgCache = MsgCacheStuff.readFromCache(event.getJDA(), event.getMessageId());
        if (msgCache == null) return;

        String messageString;
        String cachedMessageContent = msgCache.getContent();
        Member member = msgCache.getMember();
        if (member == null) return;
        if (member.getUser() == event.getJDA().getSelfUser()) return;

        if (!(cachedMessageContent.length() > 0)) return;

        if (MessageBlacklistStuff.isBlacklisted(event.getGuild(), cachedMessageContent)) return;


        messageString = "A message from " + member.getAsMention() + " got deleted in " + actionChannel.getAsMention() + ":\n\n**Content:**\n```" + cachedMessageContent + "```";
        if (cachedMessageContent.startsWith(PrefixStuff.getPrefix(event.getGuild().getIdLong()))) return;

        loggingChannel.sendMessageEmbeds(new Embed("Message deletion", messageString, Color.BLACK).build()).queue();
    }

}

