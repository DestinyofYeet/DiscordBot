package events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.Embed;
import utils.verificationLevel.CaptchaVerification;
import utils.verificationLevel.VerificationLevelStuff;

import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;

public class CaptchaSolveEventListener extends ListenerAdapter {

    public final LinkedList<CaptchaVerification> captchaList = new LinkedList<>();

    @Override
    public void onMessageReceived (@NotNull MessageReceivedEvent event){
        if (!event.isFromGuild()) return;

        for (CaptchaVerification verification: captchaList){
            if (verification.getChannelId().equals(event.getChannel().getId()) && verification.getUserId().equals(event.getMember().getId())
            && event.getMessage().getContentRaw().replaceAll(" ", "").equalsIgnoreCase(verification.getCaptchaKey())){

                Role verificationRole = event.getGuild().getRoleById(VerificationLevelStuff.getVerificationRoleId(event.getGuild().getId()));

                event.getGuild().addRoleToMember(event.getMember(), verificationRole).queue();

                event.getChannel().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_CHANNEL));

                captchaList.remove(verification);

                TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
                if (loggingChannel != null){
                    loggingChannel.sendMessageEmbeds(new Embed("Got access per captcha solving!", event.getMember().getAsMention() + " has received access to the server through solving a captcha!", Color.BLACK).build()).queue();
                }
                break;
            }
        }
    }
}
