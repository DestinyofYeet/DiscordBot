package utils.verificationLevel;

import main.Main;
import utils.sql.SQLRequest;
import utils.sql.RequestType;
import utils.sql.SQLRequestManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VerificationLevelStuff {

    private static void ensureEntry(String guildId){
        if (!hasEntry(guildId)){
            setVerificationLevel(guildId, getVerificationLevel(guildId));
        }
    }

    public static VerificationLevel getVerificationLevel(String guildId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql = "select verification_level from verificationLevel where guild_id=?";
        ArrayList<String> args = new ArrayList<>(){{
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, args);

        manager.queue(SQLRequest);

        Map<String, String> result = SQLRequest.getResult();

        if (result == null) return null;

        if (result.isEmpty()) return VerificationLevel.NOTHING;

        return VerificationLevel.getFromLevel(Integer.parseInt(result.get("verification_level")));
    }

    private static boolean hasEntry(String guildId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql = "select verification_level from verificationLevel where guild_id=?";
        ArrayList<String> args = new ArrayList<>(){{
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, args);

        manager.queue(SQLRequest);

        Map<String, String> result = SQLRequest.getResult();

        return !result.isEmpty();
    }

    public static void setVerificationLevel(String guildId, VerificationLevel verificationLevel){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        if (hasEntry(guildId)){
            sql = "update verificationLevel set verification_level=? where guild_id=?";
            args = new ArrayList<>(){{
                add(String.valueOf(verificationLevel.getLevel()));
                add(guildId);
            }};
        } else {
            sql = "insert into verificationLevel (guild_id, verification_level, verification_category_id, verification_role_id, was_locked_before, verification_text) values (?, ?, ?, ?, ?, ?)";
            args = new ArrayList<>(){{
                add(guildId);
                add(String.valueOf(verificationLevel.getLevel()));
                add(null);
                add(null);
                add(null);
                add("Tick the box to get access to the server!");
            }};
        }

        SQLRequest SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, args);

        manager.queue(SQLRequest);
    }

    public static void setVerificationCategory(String guildId, String categoryId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "update verificationLevel set verification_category_id=? where guild_id=?";
        args = new ArrayList<>(){{
            add(categoryId);
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, args);

        manager.queue(SQLRequest);
    }

    public static String getVerificationCategory(String guildId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "select verification_category_id from verificationLevel where guild_id=?";
        args = new ArrayList<>(){{
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, args);

        manager.queue(SQLRequest);

        if (SQLRequest.getResult() == null || SQLRequest.getResult().isEmpty()) return null;

        return SQLRequest.getResult().get("verification_category_id");
    }

    public static String getVerificationRoleId(String guildId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "select verification_role_id from verificationLevel where guild_id=?";
        args = new ArrayList<>(){{
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, args);

        manager.queue(SQLRequest);

        if (SQLRequest.getResult() == null || SQLRequest.getResult().isEmpty()) return null;

        return SQLRequest.getResult().get("verification_role_id");
    }

    public static void setVerificationRoleId(String guildId, String roleId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "update verificationLevel set verification_role_id=? where guild_id=?";
        args = new ArrayList<>(){{
            add(roleId);
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, args);

        manager.queue(SQLRequest);
    }

    public static void setWasLockedBefore(String guildId, LinkedList<String> list){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "update verificationLevel set was_locked_before=? where guild_id=?";
        args = new ArrayList<>(){{
            add(list.toString());
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, args);

        manager.queue(SQLRequest);
    }

    public static LinkedList<String> getWasLockedBefore(String guildId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "select was_locked_before from verificationLevel where guild_id=?";
        args = new ArrayList<>(){{
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, args);

        manager.queue(SQLRequest);

        String result = SQLRequest.getResult().get("was_locked_before");

        return new LinkedList<>(List.of(result.replace("[", "").replace("]", "").split(", ")));
    }

    public static void setVerificationText(String guildId, String text){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "update verificationLevel set verification_text=? where guild_id=?";
        args = new ArrayList<>(){{
            add(text);
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.EXECUTE, sql, args);

        manager.queue(SQLRequest);
    }

    public static String getVerificationText(String guildId){
        SQLRequestManager manager = Main.getRequestManager();

        String sql;
        ArrayList<String> args;

        ensureEntry(guildId);

        sql = "select verification_text from verificationLevel where guild_id=?";
        args = new ArrayList<>(){{
            add(guildId);
        }};

        SQLRequest SQLRequest = new SQLRequest(RequestType.RESULT, sql, args);

        manager.queue(SQLRequest);

        return SQLRequest.getResult().get("verification_text");
    }
}
