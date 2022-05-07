package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.requests.RestAction;
import org.json.JSONException;
import org.json.JSONObject;
import utils.*;
import utils.stuffs.JsonStuff;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

public class RoleReact extends CommandManager {

    public static final String commandName = "Roleract", syntax = "rolereact (messageId) (#txtchannel / txtchannelId) (emoji / emojiId) (@role / roleId)", description = "Lets you link a role to a message reaction! The emoji must be a emoji from this server and not a discord default one!";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the 'Administrator' permission!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a message id to link the reactions to!", Color.RED).build()).queue();
            return;
        }

        List<String> argsList = args.getArgs();

        if (argsList.size() < 2){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide the message channel the message is in!", Color.RED).build()).queue();
            return;
        }

        if (argsList.size() < 3){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide an emojii to react to!", Color.RED).build()).queue();
            return;
        }

        if (argsList.size() < 4){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide the role you should get when reacting!", Color.RED).build()).queue();
            return;
        }

        String messageId = argsList.get(0);

        String channelId = argsList.get(1);

        String emoteInput = argsList.get(2);

        String roleId = argsList.get(3);


        Emote emote = Constants.getEmote(event.getGuild(), emoteInput);

        if (emote == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That emote doesn't exist on this server!", Color.RED).build()).queue();
            return;
        }

        Role role = Constants.getRole(event.getGuild(), roleId);

        if (role == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That role doesn't exist", Color.RED).build()).queue();
            return;
        }

        try{
            event.getGuild().addRoleToMember(event.getGuild().getMember(event.getJDA().getSelfUser()), role);

        } catch (HierarchyException noted){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Sorry, that role is higher or on the same level than the bots role! It can't be given to other users!", Color.RED).build()).queue();
            return;
        }

        Message message = null;

        TextChannel channel = Constants.getTextChannel(event.getGuild(), channelId);

        if (channel == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "That textchannel doesn't exist", Color.RED).build()).queue();
            return;
        }

        RestAction<Message> messageData = channel.retrieveMessageById(messageId);
        message = messageData.complete();


        if (message == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "The message couldn't be found!", Color.RED).build()).queue();
            return;
        }


        String fileContent = JsonStuff.getStringFromJson(Constants.getReactionPath(), event.getGuild().getId());

        if (fileContent != null) {
            JSONObject outerJson = new JSONObject(fileContent);
            JSONObject innerJson = new JSONObject();
            innerJson.put(emote.getId(), role.getId());
            JSONObject innerJsonFromOuterJson = null;
            try {
                innerJsonFromOuterJson = new JSONObject(outerJson.getString(messageId));
            } catch (JSONException ignored){
                innerJsonFromOuterJson = new JSONObject();
            }
            for (Iterator<String> it = innerJsonFromOuterJson.keys(); it.hasNext(); ) {
                String key = it.next();
                innerJson.put(key, innerJsonFromOuterJson.get(key));
            }
            outerJson.put(messageId, innerJson.toString());


            JsonStuff.writeToJsonFile(Constants.getReactionPath(), event.getGuild().getId(), outerJson.toString());

        } else {
            JSONObject innerJson = new JSONObject();
            JSONObject outerJson = new JSONObject();
            innerJson.put(emote.getId(), role.getId());
            outerJson.put(messageId, innerJson.toString());

            JsonStuff.writeToJsonFile(Constants.getReactionPath(), event.getGuild().getId(), outerJson.toString());
        }


        message.addReaction(emote).queue();
        event.getChannel().sendMessageEmbeds(new Embed("Reacted", "Reaction added!\n\n[Click to jump to url](" + message.getJumpUrl() + ")", Color.GREEN).build()).queue();
    }
}
