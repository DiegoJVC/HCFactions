package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CmdUnshadowMute {

    @Command(names={"f unshadowmute", "faction unshadowmute", "fac unshadowmute" }, permission =  "hcfactions.mutefaction")
    public static void teamUnShadowMute(Player sender, @Param(name = "team") Faction faction) {
        TeamActionTracker.logActionAsync(faction, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of("shadowMute", "true"
        ));

        Iterator<Map.Entry<UUID, String>> mutesIterator = CmdShadowMute.getTeamShadowMutes().entrySet().iterator();

        while (mutesIterator.hasNext()) {
            Map.Entry<UUID, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(faction.getName())) {
                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the faction " + faction.getName() + ChatColor.GRAY  + ".");
    }

}