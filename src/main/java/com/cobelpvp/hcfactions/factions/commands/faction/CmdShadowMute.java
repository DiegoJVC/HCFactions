package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.google.common.collect.ImmutableMap;

import lombok.Getter;
import com.cobelpvp.atheneum.util.TimeUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CmdShadowMute {

    @Getter public static Map<UUID, String> teamShadowMutes = new HashMap<>();

    @Command(names={ "f shadowmute", "faction shadowmute", "fac shadowmute" }, permission="hcfactions.mutefaction")
    public static void teamShadowMute(Player sender, @Param(name = "team") final Faction faction, @Param(name = "time") int time) {
        int timeSeconds = time * 60;

        for (UUID player : faction.getMembers()) {
            teamShadowMutes.put(player, faction.getName());
        }

        TeamActionTracker.logActionAsync(faction, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "true",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(faction, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "true"
                ));

                Iterator<java.util.Map.Entry<UUID, String>> mutesIterator = teamShadowMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    java.util.Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(faction.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(HCFactions.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Shadow muted the faction " + faction.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + ".");
    }

}