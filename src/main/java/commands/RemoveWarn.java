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

public class RemoveWarn extends CommandManager {
    private static final Permission permission = Warn.getPermission();

    public final static String commandName = "Removewarn", syntax = "removewarn (@user / userid) (warning number / all)", description = "Lets you either delte a specific warning or all warnings. Check the history command to see the warnings.";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permission)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the ' " + permission.toString() + "' permission for this", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a user to delete a warn entry from!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        Member target = Constants.getMemberById(event.getGuild(), argsList.get(0));

        if (target == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That member doesn't exists!", Color.RED)
            .build()
            ).queue();
            return;
        }

        if (argsList.size() < 2){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Provide a warning number to remove or to remove all 'all'!", Color.RED)
            .build()
            ).queue();
            return;
        }

        int numberToRemove = 0;
        if (!argsList.get(1).equals("all")) {
            try {
                numberToRemove = Integer.parseInt(argsList.get(1));
            } catch (NumberFormatException noted) {
                event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid number!", Color.RED)
                        .build()
                ).queue();
                return;
            }
        }

        String fileContent = JsonStuff.getStringFromJson(Constants.getWarnsPath(), event.getGuild().getId());
        if (fileContent == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "No entries are present!", Color.RED)
            .build()
            ).queue();
            return;
        }

        JSONObject allMembers = new JSONObject(fileContent);
        try{
            JSONObject member = new JSONObject(allMembers.getString(target.getId()));
            if (!argsList.get(1).equals("all")) {
                if (member.length() < numberToRemove || numberToRemove <= 0) {
                    event.getChannel().sendMessageEmbeds(new Embed("Error", "That entry doesn't exist!", Color.RED)
                            .build()
                    ).queue();
                    return;
                }
                JSONObject copy = new JSONObject();
                for (String key : member.keySet()) {
                    String content = member.getString(key);
                    if (Integer.parseInt(key) >= numberToRemove && Integer.parseInt(key) != 1) {
                        copy.put(String.valueOf(Integer.parseInt(key) - 1), content);
                    } else {
                        copy.put(key, content);
                    }
                }
                allMembers.put(target.getId(), copy.toString());
                JsonStuff.writeToJsonFile(Constants.getWarnsPath(), event.getGuild().getId(), allMembers.toString());
                event.getChannel().sendMessageEmbeds(new Embed("Remove warn", "Successfully removed the warn number " + numberToRemove, Color.GREEN)
                        .build()
                ).queue();
            } else {
                allMembers.put(target.getId(), new JSONObject());
                JsonStuff.writeToJsonFile(Constants.getWarnsPath(), event.getGuild().getId(), allMembers.toString());
                event.getChannel().sendMessageEmbeds(new Embed("Remove warn", "All warns have been cleared!", Color.GREEN).build()).queue();
            }
        } catch (JSONException noted){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That user doesn't have any entries!", Color.RED).build()).queue();
        }

    }
}
