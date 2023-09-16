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

public class CmdUnmute {

    @Command(names={"f unmute", "faction unmute", "fac unmute" }, permission = "hcfactions.mutefaction")
    public static void teamUnMute(Player sender, @Param(name = "team") Faction faction) {
        TeamActionTracker.logActionAsync(faction, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of("shadowMute", "false"));

        Iterator<Map.Entry<UUID, String>> mutesIterator = CmdMute.getTeamMutes().entrySet().iterator();

        while (mutesIterator.hasNext()) {
            Map.Entry<UUID, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(faction.getName())) {
                mutesIterator.remove();
            }
        }

        sender.sendMessage(ChatColor.GRAY + "Unmuted the faction " + faction.getName() + ChatColor.GRAY  + ".");
    }

}