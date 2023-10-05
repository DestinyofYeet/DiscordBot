package utils.slashpaginator;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface SlashPaginatorLambda {

    public void execute(User user, String emote, SlashGenericPaginator paginator, Message message);
}
