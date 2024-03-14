package utils.slashpaginator;

import events.PaginatorEventListener;
import events.SlashPaginatorEventListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import utils.Embed;
import utils.Logger;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class SlashGenericPaginator {

    private String title, closedMessage, closedTitle;

    private int maxElementsPerPage, currentPage, maxPage, maxMinutesUntilDeletion;

    private Message paginatorMessage;

    private LinkedList<SlashPaginatorEntry> entries;
    private final LinkedList<SlashPaginatorReaction> reactions;

//    private MessageChannel channel;

    private SlashCommandInteractionEvent event;

    private Color color;

    private Embed embed;

    private boolean useDefaultEmotes;

    private User userRequestedThis;

    private boolean messageIsInitialed;

    private final Logger logger = new Logger("SlashGenericPaginator");;

    public SlashGenericPaginator(String title){
        this();
        this.title = title;

        maxMinutesUntilDeletion = 3;
    }

    public SlashGenericPaginator(){
        entries = new LinkedList<>();
        reactions = new LinkedList<>();

        event = null;
        paginatorMessage = null;
        embed = null;
        userRequestedThis = null;

        closedMessage = "This window is closed.";
        closedTitle = "Closed";

        color = Color.WHITE;

        maxElementsPerPage = 5;
        currentPage = 1;
        maxPage = 0;
        useDefaultEmotes = true;

        messageIsInitialed = false;

        SlashPaginatorEventListener.getInstance().addPaginator(this);
    }

    private void render(){
        maxPage = (int) Math.ceil((float) entries.size() / (float) maxElementsPerPage);

        if (maxPage < 1) maxPage = 1;

        if (event == null){
            throw new IllegalArgumentException("You need to provide a slash event to the paginator object!");
        }

        if (userRequestedThis == null){
            throw new IllegalArgumentException("You need to provide a member object who requested this paginator!");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\n");

        for (int i = maxElementsPerPage * currentPage - maxElementsPerPage; i <= currentPage * maxElementsPerPage - 1; i++){

            if (entries.size() <= i) break;

            SlashPaginatorEntry entry = entries.get(i);

            if (entry == null) continue;

            builder.append(entry.getText()).append("\n");
        }

        embed = new Embed(title + " | Page " + currentPage + "/" + maxPage, builder.toString(), color);

        if (maxElementsPerPage == 1){
            embed.setImage(entries.get(currentPage - 1).getCoverUrl());
        }
    }

    public void send(){
        if (!event.isAcknowledged()) event.deferReply().queue();
        render();

        if (!messageIsInitialed){
            event.getHook().editOriginalEmbeds(embed.build()).complete();
            if (useDefaultEmotes){
                addDefaultReactions();
            }

            applyReactions();

            event.getHook().deleteOriginal().queueAfter(this.maxMinutesUntilDeletion, TimeUnit.MINUTES, success -> {
                PaginatorEventListener.getInstance().removeMessageId(paginatorMessage.getId());
            }, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

            messageIsInitialed = true;


            return;
        }

        event.getHook().editOriginalEmbeds(embed.build()).complete();
    }

    public void close(){
        PaginatorEventListener.getInstance().removeMessageId(event.getHook().retrieveOriginal().complete().getId());

        embed = new Embed(closedTitle, closedMessage, color);

        event.getHook().editOriginalEmbeds(embed.build()).queue();
//        event.getHook().deleteOriginal().queueAfter(60, TimeUnit.SECONDS);
    }

    public void applyReactions(){
        if (paginatorMessage == null){
            paginatorMessage = event.getHook().retrieveOriginal().complete();
        }

        for (SlashPaginatorReaction reaction: reactions){
            paginatorMessage.addReaction(Emoji.fromFormatted(reaction.getEmojiString())).queue();
        }
    }

    public void clearReactions(){
        paginatorMessage.clearReactions().complete();
    }

    public void addDefaultReactions(){
        reactions.add(new SlashPaginatorReaction("⏪", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            paginatorMessage.removeReaction(Emoji.fromFormatted(emote), user).queue();

            paginator.setCurrentPage(1);

            paginator.send();
        })));

        reactions.add(new SlashPaginatorReaction("⬅️", (user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            paginatorMessage.removeReaction(Emoji.fromFormatted(emote), user).queue();

            // checks so we don't go to the 0th page
            if (paginator.getCurrentPage() - 1 >= 1){
                paginator.setCurrentPage(paginator.getCurrentPage() - 1);
            }

            paginator.send();
        }));

        reactions.add(new SlashPaginatorReaction("➡️", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            paginatorMessage.removeReaction(Emoji.fromFormatted(emote), user).queue();

            if (paginator.getCurrentPage() + 1 <= maxPage){
                paginator.setCurrentPage(paginator.getCurrentPage() + 1);
            }

            paginator.send();
        })));

        reactions.add(new SlashPaginatorReaction("⏩", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            paginatorMessage.removeReaction(Emoji.fromFormatted(emote), user).queue();

            paginator.setCurrentPage(paginator.getMaxPage());

            paginator.send();
        })));

        reactions.add(new SlashPaginatorReaction("❌", ((user, emote, paginator, message) -> {
            if (!user.getId().equals(userRequestedThis.getId())) return;

            paginator.close();
        })));
    }

    public void sortEntries(){
        LinkedList<String> list = new LinkedList<>(){{
            for (SlashPaginatorEntry entry: entries){
                add(entry.getText());
            }
        }};

        Collections.sort(list);


        entries = new LinkedList<>(){{
            for (String entry: list){
                add(new SlashPaginatorEntry(entry));
            }
        }};
    }

    public void setEvent(SlashCommandInteractionEvent event){
        this.event = event;
    }

    public void addEntry(SlashPaginatorEntry entry){
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
        if (paginatorMessage == null){
            paginatorMessage = this.event.getHook().retrieveOriginal().complete();
        }
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

    public SlashCommandInteractionEvent getEvent() {
        return event;
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

    public LinkedList<SlashPaginatorReaction> getReactions() {
        return new LinkedList<>(reactions);
    }

    public void setPaginatorMessage(Message paginatorMessage) {
        this.paginatorMessage = paginatorMessage;
    }

    public void addReaction(SlashPaginatorReaction reaction){
            reactions.add(reaction);
    }

    public int getMaxMinutesUntilDeletion() {
        return maxMinutesUntilDeletion;
    }

    public void setMaxMinutesUntilDeletion(int maxMinutesUntilDeletion) {
        this.maxMinutesUntilDeletion = maxMinutesUntilDeletion;
    }

    public void setEmbed(Embed embed) {
        this.embed = embed;
    }

    public void clearEntries(){
        this.entries = new LinkedList<>();
    }

    public String getClosedMessage() {
        return closedMessage;
    }

    public void setClosedMessage(String closedMessage) {
        this.closedMessage = closedMessage;
    }

    public String getClosedTitle() {
        return closedTitle;
    }

    public void setClosedTitle(String closedTitle) {
        this.closedTitle = closedTitle;
    }
}
