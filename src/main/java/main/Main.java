package main;

import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import utils.Config;
import utils.Constants;
import utils.Logger;
import utils.sql.SQLConnectionPool;
import utils.sql.SQLRequestManager;
import utils.uwuwhatsthis_api.UwuwhatsthisApiManager;

import javax.security.auth.login.LoginException;

public class Main {

    private SQLConnectionPool pool;
    private static Config config = null;
    private static SQLRequestManager requestManager;

    private static VerificationEventListener verificationEventListener;
    private static CaptchaSolveEventListener captchaSolveEventListener;

    private static UwuwhatsthisApiManager uwuwhatsthisApiManager;

    private static SlashCommandManager slashCommandManager;

    private final static Logger logger = new Logger("Main");

    public static void shutdown(){
        logger.info("Shutting everything down!");
        if (requestManager != null && requestManager.isConnected()){
            requestManager.stop();
        }
    }

    public Main(){

        config = new Config(Constants.getConfigPath());
        pool = new SQLConnectionPool(10, config.getSqlServer(), config.getSqlPort(), config.getSqlDatabase(), config.getSqlUsername(), config.getSqlPassword());
        requestManager = new SQLRequestManager(pool);
        requestManager.start();

        verificationEventListener = new VerificationEventListener();
        captchaSolveEventListener = new CaptchaSolveEventListener();

        uwuwhatsthisApiManager = new UwuwhatsthisApiManager();

        JDABuilder jdaBuilder = JDABuilder.createDefault(config.getBotToken());
        jdaBuilder.setActivity(Activity.playing("Type !help for help"));
        jdaBuilder.enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));


        slashCommandManager = new SlashCommandManager();


        jdaBuilder.addEventListeners(new CommandManager());
        jdaBuilder.addEventListeners(slashCommandManager);
        jdaBuilder.addEventListeners(new DeleteMessagesAfter60Seconds());
        jdaBuilder.addEventListeners(new GuildJoinEventListener());
        jdaBuilder.addEventListeners(new MessageDeleteEventListener());
        jdaBuilder.addEventListeners(new MessageEditEventListener());
        jdaBuilder.addEventListeners(new MessageReceivedEventListener());
        jdaBuilder.addEventListeners(new GuildInviteCreateEventListener());
        jdaBuilder.addEventListeners(new GuildMemberRemoveEventListener());
        jdaBuilder.addEventListeners(new GuildMemberJoinEventListener());
        jdaBuilder.addEventListeners(new GuildMessageReactionAddEventListener());
        jdaBuilder.addEventListeners(new GuildMessageReactionRemoveEventListener());
        jdaBuilder.addEventListeners(new GuildVoiceLeaveEventListener());
        jdaBuilder.addEventListeners(new DmCommandManager());
        jdaBuilder.addEventListeners(new PaginatorEventListener());
        jdaBuilder.addEventListeners(new SlashPaginatorEventListener());
        jdaBuilder.addEventListeners(new GuildInviteDeleteEventListener());
        jdaBuilder.addEventListeners(verificationEventListener);
        jdaBuilder.addEventListeners(captchaSolveEventListener);

        JDA jda = jdaBuilder.build();
    }

    public static void main(String[] args){
        new Main();
    }

    public static Config getConfig(){
        return config;
    }

    public static SQLRequestManager getRequestManager() { return requestManager;}

    public static VerificationEventListener getVerificationEventListener() {
        return verificationEventListener;
    }

    public static CaptchaSolveEventListener getCaptchaSolveEventListener() {
        return captchaSolveEventListener;
    }

    public static UwuwhatsthisApiManager getUwuwhatsthisApiManager() {
        return uwuwhatsthisApiManager;
    }

    public static SlashCommandManager getSlashCommandManager() {
        return slashCommandManager;
    }
}
