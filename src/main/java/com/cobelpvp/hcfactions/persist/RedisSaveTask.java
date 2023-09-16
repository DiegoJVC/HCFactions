package com.cobelpvp.hcfactions.persist;

import java.util.UUID;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.DBCollection;

public class RedisSaveTask extends BukkitRunnable {

    public void run() {
        save(null, false);
    }

    public static int save(final CommandSender issuer, final boolean forceAll) {
        long startMs = System.currentTimeMillis();
        int teamsSaved = Atheneum.getInstance().runRedisCommand(redis -> {

            DBCollection teamsCollection = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("Teams");
            
            int changed = 0;

            for (Faction team : HCFactions.getInstance().getFactionHandler().getTeams()) {
                if (team.isNeedsSave() || forceAll) {
                    changed++;

                    redis.set("hcf_teams." + team.getName().toLowerCase(), team.saveString(true));
                    teamsCollection.update(team.getJSONIdentifier(), team.toJSON(), true, false);
                }
                
                if (forceAll) {
                    for (UUID member : team.getMembers()) {
                        HCFactions.getInstance().getFactionHandler().setTeam(member, team, true);
                    }
                }
            }

            redis.set("RostersLocked", String.valueOf(HCFactions.getInstance().getFactionHandler().isRostersLocked()));
            if (issuer != null && forceAll) redis.save();
            return (changed);
        });

        int time = (int) (System.currentTimeMillis() - startMs);

        if (teamsSaved != 0) {
            System.out.println("Saved " + teamsSaved + " factions to Redis in " + time + "ms.");

            if (issuer != null) {
                issuer.sendMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " factions to Redis in " + time + "ms.");
            }
        }

        return (teamsSaved);
    }

}