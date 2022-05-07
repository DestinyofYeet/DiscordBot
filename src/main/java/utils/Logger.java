package utils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String identifier;

    public Logger(String identifier){
        this.identifier = identifier;
    }

    private @NotNull String getFormat(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d.M.u H:m:s")) + " - " + this.identifier;
    }

    public void info(String message){
        System.out.println("Info - " + getFormat() + ": " + message);
    }

    public void debug(String message){
        System.out.println("Debug - " + getFormat() + ": " + message);
    }

    public void error(String message){
        System.err.println("ERROR - " + getFormat() + ": " + message);
    }
}
