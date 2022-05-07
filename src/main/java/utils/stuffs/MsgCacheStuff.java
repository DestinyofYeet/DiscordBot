package utils.stuffs;

import main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;
import utils.CachedMessage;
import utils.Logger;
import utils.sql.Request;
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

        Request request = new Request(RequestType.EXECUTE, sql, data);

        manager.queue(request);
    }

    public static CachedMessage readFromCache(JDA jda, String messageId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql = "select * from messageCache where messageID=?";
        ArrayList<String> data = new ArrayList<>() {{add(messageId);}};

        Request request = new Request(RequestType.RESULT, sql, data);

        manager.queue(request);

        if (request.getResult().isEmpty()) return null;


        return new CachedMessage(request.getResult(), jda);
    }
}
