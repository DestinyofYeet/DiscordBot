package utils;

import main.Main;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.io.IOUtils;
import utils.sql.SQLRequest;
import utils.sql.RequestType;
import utils.sql.SQLRequestManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {

    public static String APEX_LEGENDS_API_BASE = "https://api.mozambiquehe.re";

    public static HashMap<String, String> getAliases() {
        // Aliases for commands, points to the file name
        return new HashMap<>() {{
            put("p", "Play");
            put("vol", "Volume");
            put("v", "Volume");
            put("s", "Skip");
            put("fuckoff", "Disconnect");
            put("f", "Disconnect");
            put("j", "Join");
            put("q", "Queue");
            put("np", "NowPlaying");
            put("clear_q", "ClearQ");
        }};
    }

    public static HashMap<String, String> getClassStringMap() {
        // you could call it aliases, but I didn't want the command to be i.e. removeWarn, rather remove_warn
        return new HashMap<>() {{
            put("botlock", "BotLock");
            put("invitewhitelist", "InviteWhitelist");
            put("logging_channel", "LoggingChannel");
            put("msgcache", "MessageCache");
            // put("ping", "PingPong");
            put("removewarn", "RemoveWarn");
            put("rolereact", "RoleReact");
            put("userinfo", "UserInfo");
            put("vctimeout", "VcTimeOut");
            put("wordblacklist", "WordBlacklist");
            put("nowplaying", "NowPlaying");
            put("send_embed", "SendEmbed");
            put("clear_queue", "ClearQ");
            put("play_next", "PlayNext");
            put("verification_level", "VerificationLevelCommand");
            put("apex_stats", "ApexStats");
        }};
    }

    // Data paths

    public static String getWarnsPath() {
        return "data/warns.json";
    }

    public static String getInviteWhitelistPath() {
        return "data/inviteWhitelist.json";
    }

    public static String getPrefixPath() {
        return "data/prefix.json";
    }

    public static String getChannelConfigurationPath() {
        return "data/channels.json";
    }



    public static String getConfigPath() {
        return "data/config.json";
    }

    public static String getReactionPath() {
        return "data/react.json";
    }

    public static String getExcludedChannelsPath() {
        return "data/excludedChannels.json";
    }

    public static String getBlacklistedWordsPath() {
        return "data/blacklistedWords.json";
    }

    public static String getBlacklistedWarnsPath() {
        return "data/blwarns.json";
    }

    // Trusted id's
    public static List<Long> getTrustedIds() {
        return new ArrayList<>() {{
            add(610543183046115330L); // me
            add(483745791744409603L); // Samu
        }};
    }

    // reads the logging channel id from the json, finds it in the guild and returns a textChannel object
    public static TextChannel getLoggingChannel(Guild guild) {
        SQLRequestManager requestManager = Main.getRequestManager();

        String sql = "select channelID from loggingChannels where guildID=?";
        ArrayList<String> data = new ArrayList<>(){{
            add(guild.getId());
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, data);

        requestManager.queue(SQLRequest);

        if (SQLRequest.getResult().isEmpty()) return null;

        return guild.getTextChannelById(SQLRequest.getResult().get("channelID"));
    }

    public static void setLoggingChannel(Guild guild, String channelID){
        SQLRequestManager requestManager = Main.getRequestManager();

        String sql = "select * from loggingChannels where guildID=?";
        ArrayList<String> data = new ArrayList<>(){{
            add(guild.getId());
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, data);

        requestManager.queue(SQLRequest);

        if (!SQLRequest.getResult().isEmpty()){
            sql = "delete from loggingChannels where guildID=?";
            ArrayList<String> data1 = new ArrayList<>(){{
                add(guild.getId());
            }};
            SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, data1);
            requestManager.queue(SQLRequest);
        }

        sql = "insert into loggingChannels (guildID, channelID) values (?, ?)";
        data.add(channelID);

        SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, data);

        requestManager.queue(SQLRequest);
    }

    public static Member getMemberById(Guild guild, String id) {
        // retrieves a member by id (doesn't use the cache, it sends a request to retrieve it)
        RestAction<Member> memberData;
        try {
            memberData = guild.retrieveMemberById(id);
        } catch (NumberFormatException noted) {
            id = id.replace("<@!", "");
            id = id.replace(">", "");
            try {
                memberData = guild.retrieveMemberById(id);
            } catch (NumberFormatException noted2) {
                return null;
            }
        }
        try {
            return memberData.complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    public static Emote getEmoteById(Guild guild, String id) {
        // gets an emote by id
        RestAction<ListedEmote> emoteData = guild.retrieveEmoteById(id);
        try {
            return emoteData.complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    public static Role getRole(Guild guild, String input) {
        // gets a role either by mention or by id
        Role role = null;
        try {
            role = guild.getRoleById(Long.parseLong(input));
        } catch (NumberFormatException noted) {
            input = input.split("&")[1];
            input = input.replace(">", "");
            role = guild.getRoleById(input);
        }

        return role;
    }

    public static TextChannel getTextChannel(Guild guild, String input) {
        // gets a textchannel either by id or mention
        TextChannel channel = null;
        try {
            channel = guild.getTextChannelById(Long.parseLong(input));
        } catch (NumberFormatException noted) {
            input = input.split("#")[1];
            input = input.replace(">", "");
            channel = guild.getTextChannelById(input);
        }

        return channel;
    }

    public static Emote getEmote(Guild guild, String input) {
        // get an emote by id or by like the :hello: tag
        Emote emote = null;
        if (input.length() < 6) return null;

        try {
            emote = Constants.getEmoteById(guild, input);
        } catch (IllegalArgumentException ignored) {
            input = input.split(":")[2];
            input = input.replace(">", "");
            emote = getEmoteById(guild, input);
        }

        return emote;
    }

    public static String getFileContent(String filePath) {
        // just give me the file contents
        File file = new File(filePath);
        try {
            return Files.readString(Paths.get(file.toURI()));
        } catch (IOException ignored) {
            return null;
        }
    }

    // sends a file in a channel
    public static void sendFile(MessageChannel channel, String content, String filename) {
        File file = new File("data/temp.txt");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
            channel.sendFile(file, filename).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }


    /**
     * Waits for the process finish before return
     *
     * @param args The command to run
     * @return A Process instance
     */
    public static Process runProcess(String args) {
        return runProcess(args, true);
    }

    /**
     * @param args The command to run
     * @param wait If the function should wait for the process to exit before it returns
     * @return A Process instance
     */
    public static Process runProcess(String args, boolean wait) {
        Logger logger = new Logger("Constants.runProcess [" + args + "]");
        Runtime runtime = Runtime.getRuntime();
        Process process;

        try {
            process = runtime.exec(args);
            logger.info("Started process");
            if (wait) {
                logger.info("Waiting for process to be completed");
                int exitCode = process.waitFor();
                logger.info("Done. Exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            logger.info("Error: " + e.getMessage());
            return null;
        }

        return process;
    }

    public static boolean sameChannelAsBot(Member member) {
        if (member.getVoiceState() == null) return false;

        if (member.getVoiceState().getChannel() == null) return false;

        Member bot = member.getGuild().getMember(member.getJDA().getSelfUser());

        if (bot == null) return false;

        if (bot.getVoiceState() == null) return false;

        if (bot.getVoiceState().getChannel() == null) return false;

        return member.getVoiceState().getChannel().getId().equals(bot.getVoiceState().getChannel().getId());
    }

    public static Class getClassNameFromCommandName(String commandName) {
        Class c = null;

        try {
            // finds the class in the commands folder, capitalizes the first letter and invokes the execute method in the class
            // does a bit of mapping to get the right class name even if an alias is used
            if (!getClassStringMap().containsKey(commandName.toLowerCase()) && !Constants.getAliases().containsKey(commandName.toLowerCase()))
                c = Class.forName("commands." + commandName.toLowerCase().substring(0, 1).toUpperCase() + commandName.substring(1));
            else {
                if (getClassStringMap().containsKey(commandName.toLowerCase()))
                    c = Class.forName("commands." + getClassStringMap().get(commandName.toLowerCase()));

                else c = Class.forName("commands." + Constants.getAliases().get(commandName.toLowerCase()));
            }
        } catch (ClassNotFoundException e) {
            return null;
        }

        return c;
    }

    public static Member getBotUserInGuild(Guild guild){
        return guild.getMember(guild.getJDA().getSelfUser());
    }

    public static String convertLongLengthInStringLength(long duration){
        int seconds = (int) (duration / 1000);
        int minutes = 0;
        int hours = 0;

        if (seconds >= 60){
            minutes = seconds / 60;
            seconds = seconds % 60;
        }

        if (minutes >= 60){
            hours = minutes / 60;
            minutes = minutes % 60;
        }

        return (hours > 0 ? (String.valueOf(hours).length() == 1 ? "0": "") + hours + ":" : "") + ((String.valueOf(minutes).length() == 1 ? "0": "") + minutes + ":") + ((String.valueOf(seconds).length() == 1 ? "0" : "") + seconds);
    }

    public static String capitalizeString(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String getOutput(InputStream stream){
        try {
            return IOUtils.toString(stream, Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean intToBool(int number){
        return number > 0;
    }
}
