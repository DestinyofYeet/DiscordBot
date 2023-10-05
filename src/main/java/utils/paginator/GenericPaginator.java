package utils.paginator;

import events.PaginatorEventListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import utils.Embed;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class GenericPaginator {

    private String title;

    private int maxElementsPerPage, currentPage, maxPage, maxMinutesUntilDeletion;

    private Message paginatorMessage;

    private LinkedList<PaginatorEntry> entries;
    private final LinkedList<PaginatorReaction> reactions;

    private MessageChannel channel;

    private Color color;

    private Embed embed;

    private boolean useDefaultEmotes;

    private User userRequestedThis;

    public GenericPaginator(String title){
        this();
        this.title = title;

        maxMinutesUntilDeletion = 3;
    }

    public GenericPaginator(){
        entries = new LinkedList<>();
        reactions = new LinkedList<>();

        channel = null;
        paginatorMessage = null;
        embed = null;
        userRequestedThis = null;

        color = Color.WHITE;

        maxElementsPerPage = 5;
        currentPage = 1;
        maxPage = 0;
        useDefaultEmotes = true;

        PaginatorEventListener.getInstance().addPaginator(this);
    }

    private void render(){
        maxPage = (int) Math.ceil((float) entries.size() / (float) maxElementsPerPage);

        if (maxPage < 1) maxPage = 1;

        if (channel == null){
            throw new IllegalArgumentException("You need to provide a channel to the paginator object!");
        }

        if (userRequestedThis == null){
            throw new IllegalArgumentException("You need to provide a member object who requested this paginator!");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\n");

        for (int i = maxElementsPerPage * currentPage - maxElementsPerPage; i <= currentPage * maxElementsPerPage - 1; i++){

            if (entries.size() <= i) break;

            PaginatorEntry entry = entries.get(i);

            if (entry == null) continue;

            builder.append(entry.getText()).append("\n");
        }

        embed = new Embed(title + " | Page " + currentPage + "/" + maxPage, builder.toString(), color);
    }

    public void send(){
        render();

        if (paginatorMessage == null){
            paginatorMessage = channel.sendMessageEmbeds(embed.build()).complete();
            if (useDefaultEmotes){
                addDefaultReactions();
            }

            applyReactions();

            paginatorMessage.delete().queueAfter(this.maxMinutesUntilDeletion, TimeUnit.MINUTES, success -> {
                PaginatorEventListener.getInstance().removeMessageId(paginatorMessage.getId());
            }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            return;
        }

        paginatorMessage.editMessageEmbeds(embed.build()).complete();
    }

    public void close(){
        PaginatorEventListener.getInstance().removeMessageId(paginatorMessage.getId());

        embed = new Embed("Closed", "This window is closed.", color);

        paginatorMessage.editMessageEmbeds(embed.build()).queue();
        paginatorMessage.delete().queueAfter(60, TimeUnit.SECONDS);
    }

    public void applyReactions(){
        for (PaginatorReaction reaction: reactions){
            paginatorMessage.addReaction(Emoji.fromFormatted(reaction.getEmojiString())).queue();
        }
    }

    public void clearReactions(){
        paginatorMessage.clearReactions().complete();
    }

    public void addDefaultReactions(){
        reactions.add(new PaginatorReaction("⏪", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            message.removeReaction(Emoji.fromFormatted(emote), user).queue();

            paginator.setCurrentPage(1);

            paginator.send();
        })));

        reactions.add(new PaginatorReaction("⬅️", (user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            message.removeReaction(Emoji.fromFormatted(emote), user).queue();

            // checks so we don't go to the 0th page
            if (paginator.getCurrentPage() - 1 >= 1){
                paginator.setCurrentPage(paginator.getCurrentPage() - 1);
            }

            paginator.send();
        }));

        reactions.add(new PaginatorReaction("➡️", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            message.removeReaction(Emoji.fromFormatted(emote), user).queue();

            if (paginator.getCurrentPage() + 1 <= maxPage){
                paginator.setCurrentPage(paginator.getCurrentPage() + 1);
            }

            paginator.send();
        })));

        reactions.add(new PaginatorReaction("⏩", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            message.removeReaction(Emoji.fromFormatted(emote), user).queue();

            paginator.setCurrentPage(paginator.getMaxPage());

            paginator.send();
        })));

        reactions.add(new PaginatorReaction("❌", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            paginator.close();
        })));
    }

    public void sortEntries(){
        LinkedList<String> list = new LinkedList<>(){{
            for (PaginatorEntry entry: entries){
                add(entry.getText());
            }
        }};

        Collections.sort(list);


        entries = new LinkedList<>(){{
            for (String entry: list){
                add(new PaginatorEntry(entry));
            }
        }};
    }

    public void setChannel(MessageChannel channel){
        this.channel = channel;
    }

    public void addEntry(PaginatorEntry entry){
        this.entries.add(entry);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public int getMaxElementsPerPage() {
        return maxElementsPerPage;
    }

    public void setMaxElementsPerPage(int maxElementsPerPage) {
        this.maxElementsPerPage = maxElementsPerPage;
    }

    public Message getPaginatorMessage() {
        return paginatorMessage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public boolean usingDefaultEmotes() {
        return useDefaultEmotes;
    }

    public void setUseDefaultEmotes(boolean useDefaultEmotes) {
        this.useDefaultEmotes = useDefaultEmotes;
    }

    public User getUserRequestedThis() {
        return userRequestedThis;
    }

    public void setUserRequestedThis(User userRequestedThis) {
        this.userRequestedThis = userRequestedThis;
    }

    public LinkedList<PaginatorReaction> getReactions() {
        return reactions;
    }

    public void setPaginatorMessage(Message paginatorMessage) {
        this.paginatorMessage = paginatorMessage;
    }

    public void addReaction(PaginatorReaction reaction){
        reactions.add(reaction);
    }

    public int getMaxMinutesUntilDeletion() {
        return maxMinutesUntilDeletion;
    }

    public void setMaxMinutesUntilDeletion(int maxMinutesUntilDeletion) {
        this.maxMinutesUntilDeletion = maxMinutesUntilDeletion;
    }
}
