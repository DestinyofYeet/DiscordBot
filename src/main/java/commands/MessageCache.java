package commands;

import main.CommandManager;
import main.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.*;
import utils.sql.SQLRequest;
import utils.sql.RequestType;
import utils.sql.SQLRequestManager;

import java.awt.Color;
import java.util.List;

public class MessageCache extends CommandManager {

    public static final String commandName = "Msgcache", syntax = "msgcache (info / clear)", description = "Lets you get info about the internal message cache. You can also clear the cache. You have to be trusted to use this command!";

    public void execute (MessageReceivedEvent event, Args args) {
        if (!Constants.getTrustedIds().contains(event.getMember().getIdLong())) {
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Sorry, but you aren't trusted to execute this command!", Color.RED).build()).queue();
            return;
        }

        SQLRequestManager manager = Main.getRequestManager();

        String sql = "select TABLE_NAME as 'Table', (DATA_LENGTH + INDEX_LENGTH) as 'Size' from information_schema.TABLES where TABLE_SCHEMA='kermitBotData' and TABLE_NAME='messageCache'";

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, null);

        manager.queue(SQLRequest);

        long fileSizeBytes = Long.parseLong(SQLRequest.getResult().get("Size"));

        long kiloBytes = 0;
        while (fileSizeBytes > 1000) {
            kiloBytes++;
            fileSizeBytes -= 1000;
        }
        long megaBytes = 0;
        while (kiloBytes > 1000) {
            megaBytes++;
            kiloBytes -= 1000;
        }
        long gigaBytes = 0;
        while (megaBytes > 1000) {
            gigaBytes++;
            megaBytes -= 1000;
        }

        sql = "select count(*) from messageCache";

        SQLRequest = new SQLRequest(RequestType.RESULT, sql, null);

        manager.queue(SQLRequest);

        int numberOfMessagesCached = Integer.parseInt(SQLRequest.getResult().get("count(*)"));

        String fileSize = null;

        if (gigaBytes > 0){
            fileSize = gigaBytes + "," + megaBytes + " Gigabytes";

        }else if (megaBytes > 0){
            fileSize = megaBytes + "," + kiloBytes + " Megabytes";

        } else if (kiloBytes > 0){
            fileSize = kiloBytes + "," + fileSizeBytes + " Kilobytes";

        } else {
            fileSize = fileSizeBytes + " Bytes";
        }

        List<String> argsList = args.getArgs();

        if (args.isEmpty() || argsList.get(0).equalsIgnoreCase("info")){
            event.getChannel().sendMessageEmbeds(new Embed("Message Cache info", "Currently cached " + numberOfMessagesCached + " messages\n\nSize of cache: " + fileSize, Color.GREEN).build()).queue();
            return;
        }

        if (argsList.get(0).equalsIgnoreCase("clear")) {

            sql = "delete from messageCache";

            SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, null);

            manager.queue(SQLRequest);

            event.getChannel().sendMessageEmbeds(new Embed("Cleared message cache successfully", "Successfully cleared internal messaged cache\n\nAmount of messages cleared: " + numberOfMessagesCached, Color.GREEN).build()).queue();
        }
    }
}
