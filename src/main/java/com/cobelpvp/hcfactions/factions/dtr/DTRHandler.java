package com.cobelpvp.hcfactions.factions.dtr;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DTRHandler extends BukkitRunnable {

    private static double[] BASE_DTR_INCREMENT = {1.5, .5, .45, .4, .36,
            .33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
            .164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
            .142, .142, .142, .142, .142, .142,
            .142, .142, .142, .142, .142};
    private static double[] MAX_DTR = {1.01, 2.01, 3.25, 3.75, 4.50, // 1 to 5
            5.25, 5.50, 5.50, 5.50, // 6 to 10
            5.50, 5.50, 5.50, 5.80, 6.05, // 11 to 15
            6.15, 6.25, 6.35, 6.45, 6.55, // 16 to 20

            6.65, 6.7, 6.85, 6.95, 7.00, // 21 to 25
            7.0, 7.0, 7.0, 7.0, 7.0, // 26 to 30
            7.0, 7.0, 7.0, 7.0, 7.0, // 31 to 35
            9, 9, 9, 9, 9}; // Padding

    private static Set<ObjectId> wasOnCooldown = new HashSet<>();

    // * 4.5 is to 'speed up' DTR regen while keeping the ratios the same.
    // We're using this instead of changing the array incase we need to change this value
    // In the future.
    public static double getBaseDTRIncrement(int teamsize) {
        return (teamsize == 0 ? 0 : BASE_DTR_INCREMENT[teamsize - 1] * HCFactions.getInstance().getMapHandler().getDtrIncrementMultiplier());
    }

    public static double getMaxDTR(int teamsize) {
        return (teamsize == 0 ? 100D : MAX_DTR[teamsize - 1]);
    }

    public static boolean isOnCooldown(Faction faction) {
        return (faction.getDTRCooldown() > System.currentTimeMillis());
    }

    public static boolean isRegenerating(Faction faction) {
        return (!isOnCooldown(faction) && faction.getDTR() != faction.getMaxDTR());
    }

    public static void markOnDTRCooldown(Faction faction) {
        wasOnCooldown.add(faction.getUniqueId());
    }

    @Override
    public void run() {
        Map<Faction, Integer> playerOnlineMap = new HashMap<>();

        for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
            if (player.hasMetadata("invisible")) {
                continue;
            }

            Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

            if (playerFaction != null && playerFaction.getOwner() != null) {
                playerOnlineMap.put(playerFaction, playerOnlineMap.getOrDefault(playerFaction, 0) + 1);
            }
        }

        playerOnlineMap.forEach((team, onlineCount) -> {
            try {
                // make sure (I guess?)
                if (isOnCooldown(team)) {
                    markOnDTRCooldown(team);
                    return;
                }

                if (wasOnCooldown.remove(team.getUniqueId())) {
                    team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your Faction is now regenerating DTR!");
                }

                double incrementedDtr = team.getDTR() + team.getDTRIncrement(onlineCount);
                double maxDtr = team.getMaxDTR();
                double newDtr = Math.min(incrementedDtr, maxDtr);
                team.setDTR(newDtr);
            } catch (Exception ex) {
                HCFactions.getInstance().getLogger().warning("Error regenerating DTR for faction " + team.getName() + ".");
                ex.printStackTrace();
            }
        });
    }

}
