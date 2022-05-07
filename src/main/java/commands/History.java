package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.stuffs.JsonStuff;

import java.awt.Color;
import java.util.List;

public class History extends CommandManager {
    // warn history

    public final static String commandName = "History", syntax = "history [@user / userid]", description = "Lets you see the history of warns for a member!";

    private static final Permission permission = Warn.getPermission();

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permission)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the '" + permission.toString() + "' permission!", Color.RED).build()).queue();
            return;
        }

        Member target;

        List<String> argsList = args.getArgs();
        if (args.isEmpty()){
            target = event.getMember();
        } else {
            target = Constants.getMemberById(event.getGuild(), argsList.get(0));
        }

        if (target == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That member doesn't exist!", Color.RED).build()).queue();
            return;
        }

        String fileContent = JsonStuff.getStringFromJson(Constants.getWarnsPath(), event.getGuild().getId());
        if (fileContent == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That member doesn't have any history!", Color.RED).build()).queue();
            return;
        }

        JSONObject allMembers = new JSONObject(fileContent);
        JSONObject currentMember;
        try{
            currentMember = new JSONObject(allMembers.getString(target.getId()));
        } catch (JSONException noted){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That member doesn't have any history!", Color.RED).build()).queue();
            return;
        }

        String message = target.getAsMention() + "**:**\n\n";
        for (int i = 1; i <= currentMember.length(); i++){
            JSONObject data = new JSONObject(currentMember.getString(String.valueOf(i)));
            Member warnedBy = Constants.getMemberById(event.getGuild(), data.getString("Warned by"));
            String warnedByString;
            if (warnedBy == null){
                warnedByString = data.getString("Warned by");
            } else {
                warnedByString = warnedBy.getAsMention();
            }
            message += "Warning " + i + ":\nDate: " + data.getString("Date") + ",\nWarned by: "
                    + warnedByString + ",\nReason: `" + data.get("Reason") + "`\n\n";
        }

        event.getChannel().sendMessageEmbeds(new Embed("Warning history of", message, Color.GREEN).build()).queue();
    }
}
