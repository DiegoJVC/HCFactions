package com.cobelpvp.hcfactions.persist.maps;

import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.persist.PersistMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PvPTimerMap extends PersistMap<Integer> {

    public PvPTimerMap() {
        super("PvPTimers", "PvPTimer", false);
        new BukkitRunnable() {

            public void run() {
                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    if (hasTimer(player.getUniqueId())) {
                        if (DTRHCFClaim.SAFE_ZONE.appliesAt(player.getLocation())) {
                            continue;
                        }

                        int newValue = getValue(player.getUniqueId()) - 1;

                        if (newValue % 60 == 0) {
                            int minutes = newValue / 60;

                            if (minutes <= 0) {
                                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your protection has expired!");
                            } else {
                                player.sendMessage(ChatColor.GREEN + "You have " + minutes + " minute" + (minutes == 1 ? "" : "s") + " of PvP protection remaining.");
                            }
                        }

                        updateValueAsync(player.getUniqueId(), newValue);
                    }
                 }
            }

        }.runTaskTimerAsynchronously(HCFactions.getInstance(), 20L, 20L);
    }

    @Override
    public String getRedisValue(Integer time) {
        return (String.valueOf(time));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer time) {
        return (time);
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update, 0);
        HCFactions.getInstance().getStartingPvPTimerMap().set(update, false);
    }

    public void createTimer(UUID update, int seconds) {
        updateValueAsync(update, seconds);
    }

    public void createStartingTimer(UUID update, int seconds) {
        createTimer(update, seconds);
        HCFactions.getInstance().getStartingPvPTimerMap().set(update, true);
    }

    public boolean hasTimer(UUID check) {
        return (getSecondsRemaining(check) > 0);
    }

    public int getSecondsRemaining(UUID check) {
        if (HCFactions.getInstance().getServerHandler().isPreEOTW()) {
            return (0);
        }

        return (contains(check) ? getValue(check) : 0);
    }

}