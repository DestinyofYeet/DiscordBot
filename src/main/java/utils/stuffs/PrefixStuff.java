package utils.stuffs;

import main.Main;
import utils.Constants;
import utils.sql.RequestType;
import utils.sql.SQLRequest;
import utils.sql.SQLRequestManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PrefixStuff {

    // prefix stuff for the bot

    public static String getPrefix(long id){

        String sqlSyntax = "select prefix from serverPrefixes where serverId=?";
        ArrayList<String> vars = new ArrayList<>(){{
            add(String.valueOf(id));
        }};

        SQLRequest request = new SQLRequest(RequestType.RESULT, sqlSyntax, vars);

        Main.getRequestManager().queue(request);

        if (request.getResult().isEmpty()) return "!"; // no prefix was set

        return request.getResult().get("prefix");
    }

    public static void setPrefix(long id, String newPrefix){
        String sqlSyntax = "select prefix from serverPrefixes where serverId=?";
        ArrayList<String> vars = new ArrayList<>(){{
            add(String.valueOf(id));
        }};

        SQLRequest request = new SQLRequest(RequestType.RESULT, sqlSyntax, vars);

        Main.getRequestManager().queue(request);

        if (request.getResult().isEmpty()) {
            sqlSyntax= "insert into serverPrefixes(serverId, prefix) values (?, ?)";
            vars = new ArrayList<>(){{
               add(String.valueOf(id));
               add(newPrefix);
            }};

        } else {
            sqlSyntax = "update serverPrefixes set prefix=? where serverId=?";
            vars = new ArrayList<>(){{
                add(newPrefix);
                add(String.valueOf(id));
            }};
        }

        request = new SQLRequest(RequestType.EXECUTE, sqlSyntax, vars);

        Main.getRequestManager().queue(request);
    }
}
