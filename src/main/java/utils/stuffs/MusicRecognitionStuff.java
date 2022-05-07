package utils.stuffs;

import net.dv8tion.jda.api.entities.Message;
import okhttp3.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class MusicRecognitionStuff {


    private String file;
    private int index;

    public MusicRecognitionStuff(String file, int index){
       this.file = file;
       this.index = index;

    }

    public Response recognize(){
        String startTime, stopTime, rawName = file.split("\\.")[0] + ".raw";

        int startSeconds = index;
        int stopSeconds = index + 5 + 1;
        int iteration = 0;

        byte[] data = null;

        do {
            stopSeconds = stopSeconds - 1;
            System.out.println("Iteration: " + iteration + " | Stop seconds: " + stopSeconds + " | Size: " + (data == null ? 0 : data.length / 1000));
            iteration ++;

            startTime = evaluateString(startSeconds);
            stopTime = evaluateString(stopSeconds);

            System.out.println("Start time: " + startTime + " | Stop time: " + stopTime);

            String ffmpegCommand = "ffmpeg -y -i " + file + " -ss " + startTime + " -to " + stopTime + " -f s16le -acodec pcm_s16le -ar 44100 -ac 1 " + rawName;

            Runtime runtime = Runtime.getRuntime();

            Process process;

            try {
                process = runtime.exec(ffmpegCommand);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

            if (process.exitValue() != 0){
                System.err.println("FFmpeg command failed! Exit code: " + process.exitValue());
                String err = null;
                try {
                    err = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();

                }

                System.err.println(err);
                return null;
            }

            Path path = Paths.get(rawName);

            try {
                data = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } while (data.length / 1000 > 500); // file can't be bigger than 500 kb

        String b64String = Base64.getEncoder().encodeToString(data);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("text/plain");

        RequestBody body = RequestBody.create(mediaType, b64String);

        Request request = new Request.Builder()
                .url("https://shazam.p.rapidapi.com/songs/detect")
                .post(body)
                .addHeader("x-rapidapi-host", "shazam.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "4c0ab78408mshc09e5161fd80903p1de12bjsnf0e261eafef9")
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println("Length of data transmitted: " + data.length / 1000 + "kb");

        try {
            Files.deleteIfExists(Path.of(rawName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String evaluateString(int seconds){
        int minutes = seconds / 60;
        int hours = minutes / 60;

        StringBuilder builder = new StringBuilder();
        if (hours >= 1){
            builder.append(String.valueOf(hours).length() > 1 ? hours : "0" + hours);
            minutes = minutes % 60;
        } else builder.append("00");

        builder.append(":");

        if (minutes >= 1){
            builder.append((String.valueOf(minutes).length() > 1 ? minutes : "0" + minutes));
            seconds = seconds % 60;
        } else builder.append("00");

        builder.append(":");

        builder.append((String.valueOf(seconds).length() > 1 ? seconds : "0" + seconds));

        return builder.toString();
    }
}
