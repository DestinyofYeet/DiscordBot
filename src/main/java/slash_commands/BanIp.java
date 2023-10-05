package slash_commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import utils.Constants;
import utils.Embed;
import utils.uwuwhatsthis_api.requests.BanIpRequest;

import java.awt.*;
import java.io.IOException;

public class BanIp {

    public final static SlashCommandData command = Commands.slash("ban_ip", "Bans a certain ip from the uwuwhatsthis.de infrastructure.")
            .addOption(OptionType.STRING, "ip", "The ip to ban")
            .addOption(OptionType.STRING, "reason", "The reason to ban the ip");

    public void execute(SlashCommandInteractionEvent event){
        if (!Constants.getTrustedIds().contains(event.getInteraction().getMember().getUser().getIdLong())){
            event.replyEmbeds(new Embed("Error", "You are not authorized to run this command!", Color.RED).build()).setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        String ip, reason;

        ip = Constants.getSlashCommandFieldIfItExistsString(event, "ip");

        if (ip == null){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You need to supply an ip to ban!", Color.RED).build()).queue();
            return;
        }

        reason = Constants.getSlashCommandFieldIfItExistsString(event, "reason");

        if (reason == null){
            event.getHook().editOriginalEmbeds(new Embed("Error", "You need to supply a reason to ban the ip!", Color.RED).build()).queue();
            return;
        }

        BanIpRequest request = new BanIpRequest(ip, reason);

        try {
            request.doRequest();
        } catch (IOException e) {
            event.getHook().editOriginalEmbeds(new Embed("Error", "Failed to ban ip! Network request failed!\n" + e.getMessage(), Color.RED).build()).queue();
            return;
        }

        if (request.getMessage() == null){
            event.getHook().editOriginalEmbeds(new Embed("ban_ip", "Failed to connect to the uwuwhatsthis api", Color.RED).build()).queue();
        } else {
            event.getHook().editOriginalEmbeds(new Embed("ban_ip", request.getMessage(), Color.WHITE).build()).queue();
        }
    }
}
