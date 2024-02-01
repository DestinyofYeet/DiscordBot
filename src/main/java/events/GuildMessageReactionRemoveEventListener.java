package events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;
import utils.Constants;
import utils.stuffs.JsonStuff;

public class GuildMessageReactionRemoveEventListener extends ListenerAdapter {
    // role react

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event){
        if (!event.isFromGuild()) return;

        if (event.getUserId().equals(event.getJDA().getSelfUser().getId())) return;

        String reactContent = JsonStuff.getStringFromJson(Constants.getReactionPath(), event.getGuild().getId());

        if (reactContent == null) return;
        String messageId = event.getMessageId();

        JSONObject json = new JSONObject(reactContent);
        if (!json.has(messageId)) return;
        String innerJsonString = json.getString(messageId);
        if (innerJsonString == null) return;

        JSONObject innerJson = new JSONObject(innerJsonString);
        String emojiId = event.getEmoji().getAsReactionCode();
        String roleId = innerJson.getString(emojiId);
        if (roleId == null) return;

        Role role = event.getGuild().getRoleById(roleId);
        if (role == null) return;

        event.getGuild().removeRoleFromMember(UserSnowflake.fromId(event.getUserId()), role).queue();

    }
}
