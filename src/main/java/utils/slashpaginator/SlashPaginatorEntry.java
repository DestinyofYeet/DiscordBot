package utils.slashpaginator;

public class SlashPaginatorEntry {

    private final String text;
    private String coverUrl;

    public SlashPaginatorEntry(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public SlashPaginatorEntry setCoverUrl(String url){
        coverUrl = url;
        return this;
    }
}
