package utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.json.JSONObject;

import java.util.Map;

public class CachedMessage {
    // Resembles a cached message

    private Guild guild;
    private final Member member;
    private final String content;
    private final TextChannel channel;

    public CachedMessage(Map<String, String> map, JDA jda){
        guild = null;
        for (Guild currentGuild: jda.getGuilds()){
            if (currentGuild.getId().equals(map.get("guildID"))) this.guild = currentGuild;
        }
        member = Constants.getMemberById(guild, map.get("authorID"));
        content = map.get("content");
        channel = guild.getTextChannelById(map.get("channelID"));
    }

    public Guild getGuild(){
        return guild;
    }

    public String getContent() {
        return content;
    }

    public Member getMember() {
        return member;
    }

    public TextChannel getChannel() {
        return channel;
    }
}

