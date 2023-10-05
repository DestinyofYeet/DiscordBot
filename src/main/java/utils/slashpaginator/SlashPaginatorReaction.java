package utils.slashpaginator;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class SlashPaginatorReaction {

    private final String emojiString;
    private final SlashPaginatorLambda lambda;

    public SlashPaginatorReaction(String emoteString, SlashPaginatorLambda lambda){
        this.emojiString = emoteString;
        this.lambda = lambda;
    }

    public SlashPaginatorReaction(Emoji emojiString, SlashPaginatorLambda lambda){
        this(emojiString.getName(), lambda);
    }

    public String getEmojiString() {
        return emojiString;
    }

    public SlashPaginatorLambda getLambda() {
        return lambda;
    }
}
