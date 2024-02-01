package commands;

import main.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Args;
import utils.Constants;
import utils.Embed;
import utils.Logger;
import utils.stuffs.MusicRecognitionStuff;
import utils.timeparsers.TimeStamp;
import utils.timeparsers.YoutubeTimeParser;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class Recognize extends CommandManager {

    public static final String commandName = "Recognize", syntax = "recognize (youtube-link with timestamp)", description = "Lets you recognize a song in a youtube-video!";

    private final Logger logger = new Logger("Recognize");

    public void execute(MessageReceivedEvent event, Args args){
        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a youtube-link with time-stamp!", Color.RED).build()).queue();
            return;
        }

        String url = args.get(0);

        if (!Constants.isUrl(url)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You didn't provide a youtube-link!", Color.RED).build()).queue();
            return;
        }


        boolean canSeek = false;
        int realTime = 0;

        YoutubeTimeParser parser = new YoutubeTimeParser(url);

        TimeStamp stamp = parser.parse();

        realTime = stamp.getSeconds() + stamp.getMinutes() * 60 + stamp.getHours() * 60 * 60;

        if (stamp.getSeconds() == -1){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Could not parse timestamp!", Color.RED).build()).queue();
            return;
        }


        Process process;

        String videoFileSyntax = "data/" + event.getGuild().getId() + "_" + event.getMember().getId() + ".%(ext)s";

        // release
//         String ytDlpPath = "/usr/local/bin/yt-dlp";

        // dev
        String ytDlpPath = "/usr/bin/yt-dlp";

        // gets the name of the file
        process = Constants.runProcess(ytDlpPath + " -f bestaudio --get-filename -o " + videoFileSyntax + " " + url);

        if (process == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Something failed while downloading the video!", Color.RED).build()).queue();
            return;
        }

        String output = null, errOutput = null;

        try {
            output = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            errOutput = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileName = output;

        // really downloads it
        process = Constants.runProcess(ytDlpPath + " -f bestaudio -o " + videoFileSyntax + " " + url);

        try {
            output = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());
            errOutput = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.debug("Output: " + output + " | Error output: " + errOutput + " | Filename: " + fileName);

        if (fileName == null){
            try{
                event.getChannel().sendMessageEmbeds(new Embed("Error", "File not found after download!", Color.RED).build()).queue();
            } catch (IllegalArgumentException noted){
                event.getChannel().sendMessageEmbeds(new Embed("Error", "File not found after download!", Color.RED).build()).queue();
                Constants.sendFile(event.getChannel(), errOutput, "error.txt");
            }

            return;
        }

        if (output.length() == 0){
            String msg = "No output has been detected! Download may have failed...";

            event.getChannel().sendMessageEmbeds(new Embed("Warning", msg, Color.ORANGE).build()).queue();

        } else if (errOutput != null && errOutput.length() > 0){
            event.getChannel().sendMessageEmbeds(new Embed("Warning", "Error output is present:\n" + errOutput, Color.ORANGE).build()).queue();
        }

        fileName = fileName.strip();

        logger.info("Filename: " + fileName);

        MusicRecognitionStuff recognize = new MusicRecognitionStuff(fileName, realTime);

        Response response = recognize.recognize();

        try {
            Files.delete(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null || !response.isSuccessful()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Failed to recognize song!", Color.RED).build()).queue();
            return;
        }

        String data;

        try {
            data = response.body().string();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Failed to decode response!", Color.RED).build()).queue();
            return;
        }

        JSONObject json = new JSONObject(data);
        if (json.getJSONArray("matches").length() == 0){
            event.getChannel().sendMessageEmbeds(new Embed("Music Recognition", "No result found!", Color.ORANGE).build()).queue();
            return;
        }

        JSONObject track = json.getJSONObject("track");
        JSONArray sections = track.getJSONArray("sections");

        StringBuilder lyrics = new StringBuilder();
        String songName = "";
        String youtubeUrl = "";
        String artistName = "";

        System.out.println(sections);

        for (Object section : sections){
            if (!(section instanceof JSONObject jsonSection))
                continue;

            switch (jsonSection.getString("type")) {
                case "LYRICS" -> {
                    JSONArray text = (jsonSection).getJSONArray("text");
                    for (Object line : text){
                        if (!(line instanceof String stringLine))
                            continue;

                        if (stringLine.length() == 0)
                            lyrics.append("\n");
                        else lyrics.append(stringLine).append("\n");
                    }

                }

                case "VIDEO" -> {
                    JSONObject ytUrl = jsonSection.getJSONObject("youtubeurl");
                    songName = ytUrl.getString("caption");
                    JSONArray actions = ytUrl.getJSONArray("actions");
                    youtubeUrl = actions.getJSONObject(0).getString("uri");
                }
                case "ARTIST" -> artistName = jsonSection.getString("name");
            }
        }

        event.getChannel().sendMessageEmbeds(new Embed("Music Recognition", "[" + songName + " by " + artistName + "](" + youtubeUrl + ")\n\nLink: " + youtubeUrl, Color.GREEN)
                .build()).queue();

        if (!lyrics.isEmpty()){
            Constants.sendFile(event.getChannel(), lyrics.toString(), "lyrics.txt");
        }

    }
}
