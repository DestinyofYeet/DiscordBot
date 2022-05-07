package utils.paginator;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface PaginatorLambda {

    public void execute(User user, String emote, GenericPaginator paginator, Message message);
}
