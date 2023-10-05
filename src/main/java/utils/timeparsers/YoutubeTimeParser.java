package utils.timeparsers;

import org.xml.sax.helpers.AttributesImpl;
import utils.Embed;
import utils.Logger;

import java.awt.*;
import java.util.Arrays;

public class YoutubeTimeParser {

    private final String url;

    private int hours, minutes, seconds;

    private final Logger logger = new Logger("YoutubeTimeParser");

    public YoutubeTimeParser(String url){
        this.url = url;

        logger.debug("Url: " + url);
    }

    public TimeStamp parse(){
        String timeToSkip = "";

        if (url.contains("&t=")){
            timeToSkip = url.split("&t=")[1];
            if (timeToSkip.contains("&")){
                timeToSkip = timeToSkip.split("&")[0];
            }

        } else if (url.contains("?t=")){
            timeToSkip = url.split("t=")[1];
        }

        if (timeToSkip.contains("m") || timeToSkip.contains("s")){
            String[] minuteSplit = timeToSkip.split("m");

            String minutes = minuteSplit[0];
            try {
                this.minutes = Integer.parseInt(minutes);
            } catch (NumberFormatException e){
                return new TimeStamp(-1, -1, -1);
            }

            System.out.println(Arrays.toString(minuteSplit));
            
            String seconds = minuteSplit[1].split("s")[0];

            try {
                this.seconds = Integer.parseInt(seconds);
            } catch (NumberFormatException e){
                return new TimeStamp(-1, -1, -1);
            }
            
        } else {
            try{
                this.seconds = Integer.parseInt(timeToSkip);
            } catch (NumberFormatException e) {
                return new TimeStamp(-1, -1, -1);
            }
        }

        return new TimeStamp(this.hours, this.minutes, this.seconds);
    }
}
