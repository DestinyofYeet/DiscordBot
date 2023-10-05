package utils.paginator;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public class PaginatorReaction {

    private final String emojiString;
    private final PaginatorLambda lambda;

    public PaginatorReaction(String emoteString, PaginatorLambda lambda){
        this.emojiString = emoteString;
        this.lambda = lambda;
    }

    public PaginatorReaction(Emoji emojiString, PaginatorLambda lambda){
        this(emojiString.getName(), lambda);
    }

    public String getEmojiString() {
        return emojiString;
    }

    public PaginatorLambda getLambda() {
        return lambda;
    }
}
