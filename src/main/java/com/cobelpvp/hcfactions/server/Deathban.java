package com.cobelpvp.hcfactions.server;

import com.mongodb.BasicDBObject;
import org.bukkit.entity.Player;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Deathban {

    private static Map<String, Integer> deathban = new LinkedHashMap<>();
    private static int defaultMinutes = 20;

    static {
        deathban.put("Famous", 3);
        deathban.put("Youtube", 6);
        deathban.put("Streamer", 6);
        deathban.put("Media", 9);
        deathban.put("Cobel", 3);
        deathban.put("Inmortal", 6);
        deathban.put("Legend", 9);
        deathban.put("Ancient", 12);
        deathban.put("Archon", 15);
        deathban.put("Basic", 18);
    }

    public static void load(BasicDBObject object) {
        deathban.clear();

        for (String key : object.keySet()) {
            if (key.equals("DEFAULT"))  {
                defaultMinutes = object.getInt(key);
            } else {
                deathban.put(key, object.getInt(key));
            }
        }
    }

    public static int getDeathbanSeconds(Player player) {
        int minutes = defaultMinutes;

        for (Map.Entry<String, Integer> entry : deathban.entrySet()) {
            if (player.hasPermission("inherit." + entry.getKey().toLowerCase()) && entry.getValue() < minutes) {
                minutes = entry.getValue();
            }
        }

        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }

}
