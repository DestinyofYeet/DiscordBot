package events;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.paginator.GenericPaginator;
import utils.paginator.PaginatorReaction;

import java.util.LinkedList;
import java.util.Objects;

public class PaginatorEventListener extends ListenerAdapter {

    private LinkedList<GenericPaginator> paginators;

    private static PaginatorEventListener instance;

    public PaginatorEventListener(){
        paginators = new LinkedList<>();
        instance = this;
    }

    public static PaginatorEventListener getInstance(){
        return instance;
    }

    public void addPaginator(GenericPaginator paginator){
        paginators.add(paginator);
    }

    public void removePaginator(GenericPaginator paginator){
        paginators.remove(paginator);
    }

    public void removeMessageId(String messageId){
        paginators.removeIf(paginator -> paginator.getPaginatorMessage().getId().equals(messageId));
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){

        if (event.getUser().getId().equals(event.getJDA().getSelfUser().getId())) return;

        for (int i = 0; i < paginators.size(); i++){
            GenericPaginator paginator = paginators.get(i);

            if (!paginator.getPaginatorMessage().getId().equals(event.getMessageId())) continue;

            for (PaginatorReaction reaction: paginator.getReactions()){
                if (Objects.equals(reaction.getEmojiString(), event.getEmoji().getAsReactionCode())){
                    reaction.getLambda().execute(event.getUser(), reaction.getEmojiString(), paginator, paginator.getPaginatorMessage());
                }
            }
        }
    }
}
