package events;

import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.stuffs.JsonStuff;
import utils.stuffs.MessageBlacklistStuff;
import utils.stuffs.PrefixStuff;
import utils.verificationLevel.CaptchaVerification;
import utils.verificationLevel.ReactionVerification;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeleteMessagesAfter60Seconds extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getChannelType() != ChannelType.TEXT)
            return;

        // if the channel got excluded, return
        List<Long> channelsExcludedIds = JsonStuff.getLongListFromJson(Constants.getExcludedChannelsPath(), event.getGuild().getId());
        if (channelsExcludedIds != null)
            if (channelsExcludedIds.contains(event.getChannel().getIdLong())) return;

        // if the channel is the logging channel, return
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel != null)
            if (event.getChannel().getIdLong() == loggingChannel.getIdLong()) return;

        // does message blacklist stuff
        if (MessageBlacklistStuff.isBlacklisted(event.getGuild(), event.getMessage().getContentRaw())){
            event.getMessage().delete().queue();
            String userdata = JsonStuff.getStringFromJson(Constants.getBlacklistedWarnsPath(), event.getGuild().getId());
            if (userdata == null){
                JSONObject object = new JSONObject();
                object.put(event.getMember().getId(), 1);
                JsonStuff.writeToJsonFile(Constants.getBlacklistedWarnsPath(), event.getGuild().getId(), object.toString());
            } else {
                JSONObject data = new JSONObject(userdata);
                try {
                    int count = data.getInt(event.getMember().getId());
                    count += 1;
                    data.put(event.getMember().getId(), count);
                } catch (JSONException ignored){
                    int count = 1;
                    data.put(event.getMember().getId(), count);
                }
                JsonStuff.writeToJsonFile(Constants.getBlacklistedWarnsPath(), event.getGuild().getId(), data.toString());
            }
            int count = new JSONObject(JsonStuff.getStringFromJson(Constants.getBlacklistedWarnsPath(), event.getGuild().getId())).getInt(event.getMember().getId());
            if (count >= 5){
                TextChannel channel = event.getChannel().asTextChannel();
                channel.getManager().getChannel().getManager().putPermissionOverride(event.getMember(), null, EnumSet.of(Permission.MESSAGE_SEND)).queue();
                channel.getManager().getChannel().getManager().putPermissionOverride(event.getMember(), EnumSet.of(Permission.MESSAGE_SEND), null).queueAfter(10, TimeUnit.MINUTES);
                event.getChannel().sendMessageEmbeds(new Embed("Restricted access", event.getMember().getAsMention() + " has been restricted access to this channel since he wrote too many blacklisted words! He will regain access to the channel in 10 Minutes!", Color.RED).build()).queue();
                if (loggingChannel != null){
                    loggingChannel.sendMessageEmbeds(new Embed("Restricted access", event.getMember().getAsMention() + " has been restricted access to the channel " + event.getChannel().getAsMention() + " since he wrote too many blacklisted words! He will regain access to the channel in 10 Minutes!", Color.BLACK).build()).queue();
                    loggingChannel.sendMessageEmbeds(new Embed("Restricted access", event.getMember().getAsMention() + " now has access to " + event.getChannel().getAsMention() + " again!", Color.BLACK).build()).queueAfter(10, TimeUnit.MINUTES);
                }
                JSONObject data = new JSONObject(JsonStuff.getStringFromJson(Constants.getBlacklistedWarnsPath(), event.getGuild().getId()));
                data.put(event.getMember().getId(), 0);
                JsonStuff.writeToJsonFile(Constants.getBlacklistedWarnsPath(), event.getGuild().getId(), data.toString());
            }
            return;
        }


        // excludes paginators
        if (event.getAuthor().equals(event.getJDA().getSelfUser())){
            for (MessageEmbed embed : event.getMessage().getEmbeds()){
                for (MessageEmbed.Field field : embed.getFields()) {
                    if (field.getName() == null) continue;
                    if (field.getName().contains("|") && field.getName().contains("Page") && field.getName().contains("/")){
                        return;
                    }
                }

            }
        }

        // excludes verifications
        for (ReactionVerification verification: Main.getVerificationEventListener().reactionVerificationList){
            if (verification.getChannelId().equals(event.getChannel().getId())) return;
        }

        for (CaptchaVerification verification: Main.getCaptchaSolveEventListener().captchaList){
            if (verification.getChannelId().equals(event.getChannel().getId())) return;
        }


        // delete messages from the bot and messages that start with the prefix of the current guild
        try {
            if (event.getAuthor().equals(event.getJDA().getSelfUser())) {
                event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE).ignore(ErrorResponse.UNKNOWN_CHANNEL));
            } else if (event.getMessage().getContentRaw().startsWith(PrefixStuff.getPrefix(event.getGuild().getIdLong()))) {
                event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE).ignore(ErrorResponse.UNKNOWN_CHANNEL));
            } else if (event.getMessage().getContentRaw().equals("!help")){
                event.getMessage().delete().queueAfter(60, TimeUnit.SECONDS, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE).ignore(ErrorResponse.UNKNOWN_CHANNEL));
            }
        } catch (Exception ignored) {}
    }
}
