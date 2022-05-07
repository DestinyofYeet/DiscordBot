package utils;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private final String filePath;
    private final JSONObject content;

    private String botToken;

    private String youtubeDevKey;
    private String youtubeApplicationName;

    private String sqlServer;
    private String sqlPort;
    private String sqlDatabase;
    private String sqlUsername;
    private String sqlPassword;

    private String apexLegendsKey;

    public Config(String filePath){
        this.filePath = filePath;

        String content = null;

        try {
            content = new String(Files.readAllBytes(Path.of(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            this.content = null;
            return;
        }

        this.content = new JSONObject(content);
        parseContent();
    }

    private void parseContent(){
        this.botToken = this.content.getString("token");

        this.youtubeDevKey = this.content.getString("youtube-dev-key");
        this.youtubeApplicationName = this.content.getString("youtube-application-name");

        this.sqlServer = this.content.getString("sql-server");
        this.sqlPort = this.content.getString("sql-port");
        this.sqlDatabase = this.content.getString("sql-database");
        this.sqlUsername = this.content.getString("sql-username");
        this.sqlPassword = this.content.getString("sql-password");

        this.apexLegendsKey = this.content.getString("apex-legends-key");
    }

    public String getFilePath() {
        return filePath;
    }

    public JSONObject getContent() {
        return content;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getYoutubeDevKey() {
        return youtubeDevKey;
    }

    public String getSqlServer() {
        return sqlServer;
    }

    public String getSqlPort() {
        return sqlPort;
    }

    public String getSqlDatabase() {
        return sqlDatabase;
    }

    public String getSqlUsername() {
        return sqlUsername;
    }

    public String getSqlPassword() {
        return sqlPassword;
    }

    public String getYoutubeApplicationName() {
        return youtubeApplicationName;
    }

    public String getApexLegendsKey() {
        return apexLegendsKey;
    }
}
