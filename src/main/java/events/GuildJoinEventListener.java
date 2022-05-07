package events;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinEventListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        System.out.println("Bot joined guild " + event.getGuild().getName());
    }
}
