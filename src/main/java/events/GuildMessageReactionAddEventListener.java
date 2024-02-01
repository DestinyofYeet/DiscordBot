package events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Constants;
import utils.stuffs.JsonStuff;

public class GuildMessageReactionAddEventListener extends ListenerAdapter {
    // role react

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        if (!event.isFromGuild()) return;

        if (event.getUserId().equals(event.getJDA().getSelfUser().getId())) return;

        String reactContent = JsonStuff.getStringFromJson(Constants.getReactionPath(), event.getGuild().getId());
        if (reactContent == null) return;
        String messageId = event.getMessageId();
        String emojiId;
        try {
            emojiId = event.getEmoji().getAsReactionCode();
        } catch (IllegalStateException ignored){
            return;
        }

        JSONObject json = new JSONObject(reactContent);
        String innerJsonString;
        try{
            innerJsonString = json.getString(messageId);
        } catch (JSONException ignored){
            return;
        }

        if (innerJsonString == null) return;

        JSONObject innerJson = new JSONObject(innerJsonString);
        String roleId = innerJson.getString(emojiId);
        if (roleId == null) return;

        Role role = event.getGuild().getRoleById(roleId);
        if (role == null) return;

        event.getGuild().addRoleToMember(event.getMember(), role).queue();
    }
}
