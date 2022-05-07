package commands;

import main.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import utils.Args;
import utils.Constants;
import utils.Embed;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Poll extends CommandManager {
    private final Permission permissionNeeded = Permission.ADMINISTRATOR;

    public static final String commandName = "Poll", syntax = "poll (#textchannel / id) (Title) (description) [List of emojis]", description = "Lets you make a poll. Put the topic and description in \" (Quotes) to be able to do some spaced topics and description. If you don't provide emojis it will just be yes or no.";

    public void execute(MessageReceivedEvent event, Args args){
        if (!event.getMember().hasPermission(permissionNeeded)){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Insufficient permissions! You need the " + permissionNeeded.toString() + " permission!", Color.RED).build()).queue();
            return;
        }

        if (args.isEmpty()){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a text channel!", Color.RED).build()).queue();
            return;
        }

        TextChannel targetChannel = Constants.getTextChannel(event.getGuild(), args.get(0));

        if (targetChannel == null){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "Invalid textchannel!", Color.RED).build()).queue();
            return;
        }

        if (args.size() < 2){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a topic!", Color.RED).build()).queue();
            return;
        }

        String topic = args.get(1);

        if (args.size() < 3){
            event.getChannel().sendMessageEmbeds(new Embed("Error", "You need to provide a description!", Color.RED).build()).queue();
            return;
        }

        String description = args.get(2);

        List<String> reactionEmotes = new ArrayList<>();

        if (args.size() < 4){
            reactionEmotes.add("✅");
            reactionEmotes.add("\uD83D\uDEAB");
        }

        for (int i=3; args.size() > i; i++){
            reactionEmotes.add(args.get(i));
        }

        List<String> stringEmotes = new ArrayList<>();


        List<Emote> emotesToReact = new ArrayList<Emote>(){{
           for (String emoteString: reactionEmotes){
               if (emoteString.length() < 6){stringEmotes.add(emoteString); continue;}
               Emote emote = Constants.getEmote(event.getGuild(), emoteString);
               if (emote != null) add(emote);
           }
        }};
        event.getChannel().sendMessageEmbeds(new Embed("Poll", "Poll sent in " + targetChannel.getAsMention() + "!", Color.GREEN).build()).queue();
        targetChannel.sendMessageEmbeds(new Embed("Poll: " + topic, description, Color.GREEN).build()).queue(m -> {
            for (Emote emote: emotesToReact) m.addReaction(emote).queue();
            for (String stringEmoteString: stringEmotes) m.addReaction(stringEmoteString).queue();
        });

    }
}
