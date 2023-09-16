package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import lombok.Getter;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.TimeUtils;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CmdMute {

    @Getter private static Map<UUID, String> teamMutes = new HashMap<>();

    @Command(names={ "f mute", "faction mute", "fac mute" }, permission="hcfactions.mutefaction")
    public static void teamMute(Player sender, @Param(name="team") final Faction faction, @Param(name="time") int time, @Param(name="reason", wildcard=true) String reason) {
        int timeSeconds = time * 60;

        for (UUID player : faction.getMembers()) {
            teamMutes.put(player, faction.getName());

            Player bukkitPlayer = HCFactions.getInstance().getServer().getPlayer(player);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction has been muted for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
            }
        }

        TeamActionTracker.logActionAsync(faction, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "false",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(faction, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "false"
                ));

                Iterator<Map.Entry<UUID, String>> mutesIterator = teamMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(faction.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(HCFactions.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Muted the faction " + faction.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
    }

}