package utils.stuffs;

import net.dv8tion.jda.api.entities.Guild;
import utils.Constants;

import java.util.List;

public class MessageBlacklistStuff {
    // Checks if a blacklisted word in a message

    public static boolean isBlacklisted(Guild guild, String msgContent){
        List<String> blacklistedWords = JsonStuff.getStringListFromJson(Constants.getBlacklistedWordsPath(), guild.getId());
        if (blacklistedWords == null) return false; // check if the guild has blacklisted words
        if (msgContent.startsWith(PrefixStuff.getPrefix(guild.getIdLong()) + "wordblacklist")) return false; // check if the messages starts with the prefix


        for (String blacklistedWord : blacklistedWords) {
            if (msgContent.toLowerCase().contains(blacklistedWord.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
