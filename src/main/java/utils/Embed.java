package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class Embed {
    // improved embed builder (embed builder v 2)

    private EmbedBuilder builder;

    public Embed(String title, String message, Color color){
        try{
            builder = new EmbedBuilder();
            builder.addField(title, message, false);
            builder.setColor(color);
            builder.setTimestamp(Instant.now());

        } catch (IllegalArgumentException noted){
            builder = null;
            throw noted;
        }

    }

    public Embed(String title, String message, Color color, String additionalFooter){
        try{
            EmbedBuilder builder = new EmbedBuilder();
            builder.addField(title, message, false);
            builder.setColor(color);
            builder.setFooter(additionalFooter);
            builder.setTimestamp(Instant.now());

        } catch (IllegalArgumentException noted){
            builder = null;
            throw noted;
        }

    }

    public Embed setImage(String url){
        builder.setImage(url);
        return this;
    }

    public Embed setAuthor(String text, String url){
        builder.setAuthor(text, null, url);
        return this;
    }

    public Embed setThumbnail(String url){
        builder.setThumbnail(url);
        return this;
    }

    public Embed addField(String title, String message, boolean inline){
        try{
            builder.addField(title, message, inline);
            return this;

        } catch (IllegalArgumentException noted){
            return null;
        }
    }

    public Embed addField(String title, String message){
        return addField(title,message, false);
    }

    public MessageEmbed build(){
        if (builder != null)
            return builder.build();
        else
            return null;
    }

}
