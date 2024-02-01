package utils.uwuwhatsthis_api.requests.doubledouble.results;

public class SongSearchResult {
    private final String type, link, links, album, artist, name, coverUrl;

    public SongSearchResult(String type, String link, String links, String album, String artist, String name, String cover_url) {
        this.type = type;
        this.link = link;
        this.links = links;
        this.album = album;
        this.artist = artist;
        this.name = name;
        this.coverUrl = cover_url;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public String getLinks() {
        return links;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
