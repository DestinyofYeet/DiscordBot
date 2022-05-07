package utils.stuffs;

import org.json.JSONException;
import org.json.JSONObject;
import utils.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrefixStuff {

    // prefix stuff for the bot

    public static String getPrefix(long id){

        File file = new File(Constants.getPrefixPath());

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);

            if (json.get(String.valueOf(id)) instanceof String){
                String prefix = (String) json.get(String.valueOf(id));
                return prefix;

            }


        } catch (IOException e){
            e.printStackTrace();

        } catch (JSONException e){
            setPrefix(id, "!");
            return "!";
        }

        return "!";
    }

    public static void setPrefix(long id, String newPrefix){
        JsonStuff.writeToJsonFile(Constants.getPrefixPath(), String.valueOf(id), newPrefix);
    }
}
