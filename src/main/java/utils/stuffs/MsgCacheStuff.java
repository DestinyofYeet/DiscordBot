package utils.stuffs;

import main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.CachedMessage;
import utils.Logger;
import utils.sql.SQLRequest;
import utils.sql.RequestType;
import utils.sql.SQLRequestManager;

import java.util.ArrayList;

public class MsgCacheStuff {

    // Stuff to cache a message

    private static final Logger logger = new Logger("MsgCacheStuff");

    public static void writeToCache(MessageReceivedEvent event){
        if (!event.isFromGuild()) return;

        SQLRequestManager manager = Main.getRequestManager();

        String sql = "insert into messageCache (messageID, guildID, authorID, channelID, content) values (?, ?, ?, ?, ?)";
        ArrayList<String> data = new ArrayList<>(){{
            add(event.getMessageId());
            add(event.getGuild().getId());
            add(event.getAuthor().getId());
            add(event.getChannel().getId());
            add(event.getMessage().getContentRaw());
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, data);

        manager.queue(SQLRequest);
    }

    public static CachedMessage readFromCache(JDA jda, String messageId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql = "select * from messageCache where messageID=?";
        ArrayList<String> data = new ArrayList<>() {{add(messageId);}};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, data);

        manager.queue(SQLRequest);

        if (SQLRequest.getResult().isEmpty()) return null;


        return new CachedMessage(SQLRequest.getResult(), jda);
    }
}
