package events;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.paginator.PaginatorReaction;
import utils.slashpaginator.SlashGenericPaginator;
import utils.slashpaginator.SlashPaginatorReaction;

import java.util.LinkedList;
import java.util.Objects;

public class SlashPaginatorEventListener extends ListenerAdapter {

    private LinkedList<SlashGenericPaginator> paginators;

    private static SlashPaginatorEventListener instance;

    public SlashPaginatorEventListener(){
        paginators = new LinkedList<>();
        instance = this;
    }

    public static SlashPaginatorEventListener getInstance(){
        return instance;
    }

    public void addPaginator(SlashGenericPaginator paginator){
        paginators.add(paginator);
    }

    public void removePaginator(SlashGenericPaginator paginator){
        paginators.remove(paginator);
    }

    public void removeMessageId(String messageId){
        paginators.removeIf(paginator -> paginator.getPaginatorMessage().getId().equals(messageId));
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){

        if (event.getUser().getId().equals(event.getJDA().getSelfUser().getId())) return;

        for (SlashGenericPaginator paginator : (LinkedList<SlashGenericPaginator>) paginators.clone()) {
            if (!paginator.getPaginatorMessage().getId().equals(event.getMessageId()))
                continue;

            for (SlashPaginatorReaction reaction : paginator.getReactions()) {
                if (Objects.equals(reaction.getEmojiString(), event.getEmoji().getAsReactionCode())) {
                    reaction.getLambda().execute(event.getUser(), reaction.getEmojiString(), paginator, paginator.getPaginatorMessage());
                }
            }
        }
    }
}
