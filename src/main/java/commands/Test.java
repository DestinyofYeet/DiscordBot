package commands;

import events.PaginatorEventListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.paginator.GenericPaginator;
import utils.paginator.PaginatorEntry;
import utils.paginator.PaginatorReaction;

import java.awt.*;

public class Test {
    private final StringBuilder helpTextBuilder = new StringBuilder()
            .append("Select a category of commands you want help for!").append("\n")
            .append("\uD83D\uDEE1️: Moderation").append("\n")
            .append("\uD83C\uDFB5: Music").append("\n");

    public void execute(MessageReceivedEvent event, Args args){
        GenericPaginator mainPaginator = new GenericPaginator("Help command");
        mainPaginator.setChannel(event.getChannel());
        mainPaginator.setUserRequestedThis(event.getMember().getUser());
        mainPaginator.setUseDefaultEmotes(false);
        mainPaginator.setColor(Color.WHITE);

        mainPaginator.addEntry(new PaginatorEntry(helpTextBuilder.toString()));



        mainPaginator.addReaction(new PaginatorReaction("\uD83D\uDEE1️", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(paginator.getUserRequestedThis().getId())) return;

            GenericPaginator moderationPaginator = new GenericPaginator("Moderation commands");
            moderationPaginator.setChannel(event.getChannel());
            moderationPaginator.setUserRequestedThis(user);
            moderationPaginator.setColor(Color.WHITE);
            moderationPaginator.setPaginatorMessage(message);

            moderationPaginator.addEntry(new PaginatorEntry("Moderation stuff"));

            moderationPaginator.addReaction(new PaginatorReaction("\uD83D\uDD19", (member1, emote1, paginator1, message1) -> {
                if (!member1.getId().equals(paginator1.getUserRequestedThis().getId())) return;

                PaginatorEventListener.getInstance().removePaginator(paginator1);

                moderationPaginator.clearReactions();

                mainPaginator.applyReactions();
                mainPaginator.send();
            }));


            mainPaginator.clearReactions();
            moderationPaginator.addDefaultReactions();
            moderationPaginator.applyReactions();

            moderationPaginator.send();
        })));

        mainPaginator.addReaction(new PaginatorReaction("\uD83C\uDFB5", ((member, emote, paginator, message) -> {
            if (!member.getId().equals(paginator.getUserRequestedThis().getId())) return;


            GenericPaginator musicPaginator = new GenericPaginator("Music commands");
            musicPaginator.setChannel(event.getChannel());
            musicPaginator.setUserRequestedThis(member);
            musicPaginator.setColor(Color.WHITE);
            musicPaginator.setPaginatorMessage(message);

            musicPaginator.addEntry(new PaginatorEntry("Music stuff"));

            musicPaginator.addReaction(new PaginatorReaction("\uD83D\uDD19", (member1, emote1, paginator1, message1) -> {
                if (!member1.getId().equals(paginator1.getUserRequestedThis().getId())) return;

                PaginatorEventListener.getInstance().removePaginator(paginator1);

                musicPaginator.clearReactions();

                mainPaginator.applyReactions();
                mainPaginator.send();
            }));

            mainPaginator.clearReactions();
            musicPaginator.addDefaultReactions();
            musicPaginator.applyReactions();

            musicPaginator.send();
        })));

        mainPaginator.addReaction(new PaginatorReaction("❌", ((member, emote, paginator, message) -> {
            if (!member.getId().equals(paginator.getUserRequestedThis().getId())) return;

            paginator.close();
        })));

        mainPaginator.send();
    }
}
