package utils.dd_download;

import net.dv8tion.jda.api.entities.User;

public class MusicDownloadEntry {
    private String id;
    private User user;

    private final OnMusicDownloadCallback callback;
    public MusicDownloadEntry(String downloadId, User requestedUser, OnMusicDownloadCallback callback){
        id = downloadId;
        user = requestedUser;
        this.callback = callback;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public OnMusicDownloadCallback getCallback() {
        return callback;
    }
}
