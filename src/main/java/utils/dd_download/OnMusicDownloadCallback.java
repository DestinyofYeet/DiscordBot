package utils.dd_download;

public interface OnMusicDownloadCallback {

    public void execute(MusicDownloadEntry entry, boolean success, String status);
}
