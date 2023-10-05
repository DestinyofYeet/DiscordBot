package events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import utils.Constants;
import utils.Embed;
import utils.verificationLevel.ReactionVerification;
import utils.verificationLevel.VerificationLevelStuff;

import java.awt.*;
import java.util.LinkedList;

public class VerificationEventListener extends ListenerAdapter {

    public final LinkedList<ReactionVerification> reactionVerificationList = new LinkedList<>();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.isFromGuild()) return;

        for (ReactionVerification reactionVerification: reactionVerificationList){
            if (reactionVerification.getMessageId().equals(event.getMessageId()) && reactionVerification.getUserId().equals(event.getUserId())
            && reactionVerification.getReactionId().equals(event.getReaction().getEmoji().getAsReactionCode())){
                Role verificationRole = event.getGuild().getRoleById(VerificationLevelStuff.getVerificationRoleId(event.getGuild().getId()));

                event.getGuild().addRoleToMember(event.getMember(), verificationRole).queue();

                event.getChannel().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_CHANNEL));

                reactionVerificationList.remove(reactionVerification);

                TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
                if (loggingChannel != null){
                    loggingChannel.sendMessageEmbeds(new Embed("Got access per message reaction!", event.getMember().getAsMention() + " has received access to the server through the message reaction method!", Color.BLACK).build()).queue();
                }
                break;
            }
        }
    }
}
