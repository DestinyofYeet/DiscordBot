package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.stuffs.JsonStuff;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Warn extends CommandManager {
    private static final Permission permission = Permission.KICK_MEMBERS;

    public static final String commandName = "Warn", syntax = "warn (@user) (reason)", description = "Lets you warn a user! Additionally the member will get a dm.";


    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permission)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the ' " + permission.toString() + "' permission for this", Color.RED).build()).queue();
            return;
        }
        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a user to warn", Color.RED).build()).queue();
            return;
        }
        Member target = Constants.getMemberById(event.getGuild(), args.get(0));

        if (target == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That member doesn't exist", Color.RED).build()).queue();
            return;
        }

        String reason = "";

        if (args.size() < 2){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Provide a reason for your warn!", Color.RED).build()).queue();
            return;
        }

        for (String part: args.getArgs()){
            if (!part.equals(args.get(0)))
                reason = reason + " " + part;
        }
        reason = reason.replaceFirst(" ", "");

        TextChannel loggingChannel = Constants.getLoggingChannel(event.getGuild());

        String fileContent = JsonStuff.getStringFromJson(Constants.getWarnsPath(), event.getGuild().getId());

        if (fileContent == null){
            JsonStuff.writeToJsonFile(Constants.getWarnsPath(), event.getGuild().getId(), "{}");
            fileContent = JsonStuff.getStringFromJson(Constants.getWarnsPath(), event.getGuild().getId());
        }

        JSONObject allMemberWarns = new JSONObject(fileContent);
        JSONObject warnData = new JSONObject();
        JSONObject memberJson;
        try {
            memberJson = new JSONObject(allMemberWarns.getString(target.getId()));
        } catch (JSONException noted){
            memberJson = new JSONObject();
        }
        int nextNumber = memberJson.length() + 1;
        warnData.put("Warned by", event.getMember().getId());
        warnData.put("Reason", reason);
        warnData.put("Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
        memberJson.put(String.valueOf(nextNumber), warnData.toString());
        allMemberWarns.put(target.getId(), memberJson.toString());
        JsonStuff.writeToJsonFile(Constants.getWarnsPath(), event.getGuild().getId(), allMemberWarns.toString());

        String numberString;
        switch (nextNumber){
            case 1:
                numberString = "This is the users 1st warn!";
                break;

            case 2:
                numberString = "This is the users 2nd warn!";
                break;

            case 3:
                numberString = "This is the users 3rd warn!";
                break;

            default:
                numberString = "This is the users " + nextNumber + "th warn!";
        }

        event.getChannel().sendMessageEmbeds(new Embed("Warned", "Successfully warned " + target.getAsMention() + " for: \n```" + reason + "```\n" + numberString, Color.GREEN).build()).queue();
        if (loggingChannel != null){
            loggingChannel.sendMessageEmbeds(new Embed("Warned", target.getAsMention() + " has been warned by " + event.getMember().getAsMention() + " for:\n```" + reason + "```", Color.BLACK).build()).queue();
        }
        target.getUser().openPrivateChannel().complete().sendMessageEmbeds(new Embed("Warned", "You have been warned on the server `" + event.getGuild().getName() + "` with following reason: \n```" + reason + "```\nPlease behave respectfully and follow the server rules!", Color.RED)
        .build()
        ).queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
    }

    public static Permission getPermission() {
        return permission;
    }
}
