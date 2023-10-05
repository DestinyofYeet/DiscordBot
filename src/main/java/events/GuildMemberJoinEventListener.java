package events;

import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.FileUpload;
import org.json.JSONObject;
import utils.Constants;
import utils.Embed;
import utils.Logger;
import utils.verificationLevel.CaptchaVerification;
import utils.verificationLevel.ReactionVerification;
import utils.verificationLevel.VerificationLevel;
import utils.verificationLevel.VerificationLevelStuff;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class GuildMemberJoinEventListener extends ListenerAdapter {
    private static final Logger logger = new Logger("GuildMemberJoinEventListener");

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event){
        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());
        if (loggingChannel != null){
            loggingChannel.sendMessageEmbeds(new Embed("Joined", event.getMember().getAsMention() + " with id " + event.getMember().getId() + " has joined the server!", Color.BLACK).build()).queue();
        }

        VerificationLevel verificationLevel = VerificationLevelStuff.getVerificationLevel(event.getGuild().getId());

        if (verificationLevel == null) {
            logger.error("Failed to get verification level for guild with id " + event.getGuild().getId());
            return;
        }

        if (verificationLevel.getLevel() != 0) doVerification(event, verificationLevel);


    }

    private void doVerification(GuildMemberJoinEvent event, VerificationLevel verificationLevel){
        String verificationCategoryId = VerificationLevelStuff.getVerificationCategory(event.getGuild().getId());

        Category verificationCategory;

        if (verificationCategoryId == null || event.getGuild().getCategoryById(verificationCategoryId) == null){
            verificationCategory = event.getGuild().createCategory("Verification").complete();
            event.getGuild().modifyCategoryPositions().selectPosition(verificationCategory).moveTo(0).queue();
            verificationCategoryId = verificationCategory.getId();
        };

        verificationCategory = event.getGuild().getCategoryById(verificationCategoryId);

        TextChannel currentUserChannel = verificationCategory.createTextChannel(event.getMember().getEffectiveName() + "#" + event.getMember().getUser().getDiscriminator()).complete();

        currentUserChannel.getManager().putPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL)).queue();


        currentUserChannel.getManager().putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null).queue();

        if (verificationLevel.getLevel() == 1){
            currentUserChannel.sendMessage(event.getMember().getAsMention() + "\n" + VerificationLevelStuff.getVerificationText(event.getGuild().getId())).queue(message -> {
                Main.getVerificationEventListener().reactionVerificationList.add(new ReactionVerification(event.getMember().getId(), message.getId(), currentUserChannel.getId(), "✅"));
                message.addReaction(Emoji.fromFormatted("✅")).queue();
            });

        } else if (verificationLevel.getLevel() == 2){
            String filename = "data/" + event.getMember().getId() + "_" + event.getGuild().getId();
            Process process = Constants.runProcess("python3.8 bin/gen_captcha.py " + filename);

            String output = Constants.getOutput(process.getInputStream());

            if (output == null || output.length() == 0){
                logger.error("Failed to generate captcha! Error: " + Constants.getOutput(process.getErrorStream()));
                return;
            }

            output = output.trim();

            JSONObject json = new JSONObject(output);

            Main.getCaptchaSolveEventListener().captchaList.add(new CaptchaVerification(event.getMember().getId(), currentUserChannel.getId(), json.getString("key")));

            currentUserChannel.sendMessageEmbeds(new Embed("Captcha required!", "Please solve this captcha to get access to the server!", Color.BLACK).build()).queue();
            currentUserChannel.sendFiles(FileUpload.fromData(new File(json.getString("path")), "captcha.png")).queue();

            logger.info((new File(json.getString("path")).delete() ? "Successfully deleted" : "Failed to delete") + " captcha image!");
        }

        currentUserChannel.delete().queueAfter(5, TimeUnit.MINUTES, null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_CHANNEL));
    }
}
