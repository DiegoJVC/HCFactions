package com.cobelpvp.hcfactions.factions.track;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public final class TeamActionTracker {

    private static final File logFileRoot = new File(new File("hcflogs"), "teamactiontracker");
    @Getter
    @Setter
    private static boolean databaseLogEnabled = true;

    public static void logActionAsync(Faction faction, TeamActionType actionType, Map<String, Object> params) {
        if (faction.isLoading()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(HCFactions.getInstance(), () -> {
            logActionToFile(faction, actionType, params);

            if (databaseLogEnabled && actionType.isLoggedToDatabase()) {
                logActionToDatabase(faction, actionType, params);
            }
        });
    }

    private static void logActionToFile(Faction faction, TeamActionType actionType, Map<String, Object> params) {
        File teamLogFolder = new File(logFileRoot, faction.getName());
        File teamLogFile = new File(teamLogFolder, (actionType.isLoggedToDatabase() ? "general" : "misc") + ".log");

        try {
            StringBuilder logLine = new StringBuilder();

            logLine.append('[');
            logLine.append(DateTimeFormatter.ISO_INSTANT.format(Instant.now())); // ISO 8601
            logLine.append(", ");
            logLine.append(actionType.getInternalName());
            logLine.append("] ");

            params.forEach((key, value) -> {
                logLine.append(key);
                logLine.append(": ");
                logLine.append(value);
                logLine.append(' ');
            });

            logLine.append('\n');

            teamLogFile.getParentFile().mkdirs();
            teamLogFile.createNewFile();

            Files.append(
                    logLine.toString(),
                    teamLogFile,
                    Charsets.UTF_8
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void logActionToDatabase(Faction faction, TeamActionType actionType, Map<String, Object> params) {
        BasicDBObject entry = new BasicDBObject();

        entry.put("teamId", faction.getUniqueId().toString());
        entry.put("teamName", faction.getName());
        entry.put("time", new Date());
        entry.put("type", actionType.getInternalName());

        BasicDBObject paramsJson = new BasicDBObject();

        // we manually serialize this so we use .toString
        // instead of Mongo's serialization (ex UUID -> binary)
        params.forEach((key, value) -> {
            paramsJson.put(key, value.toString());
        });

        entry.put("params", paramsJson);

        // we embed the entire team json here :(
        // this is bad and uses a lot of data, but
        // the web dev team (Ariel) will have to do
        // less work if we embed it.
        entry.put("teamAfterAction", faction.toJSON());

        DB db = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME);
        db.getCollection("TeamActions").insert(entry);
    }

}