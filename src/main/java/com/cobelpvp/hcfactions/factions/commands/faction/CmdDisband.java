package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.util.ColorText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdDisband {

    @Command(names={"f disband", "faction disband", "fac disband" }, permission="")
    public static void teamDisband(Player player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (faction == null){
            player.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (!faction.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the faction to disband it!");
            return;
        }

        if (faction.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your faction while raidable.");
            return;
        }

        String watcherNameStars = ChatColor.RED + "";
        if (faction.isOwner(player.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "**";
        } else if (faction.isCoLeader(player.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "**";
        } else if (faction.isCaptain(player.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "*";
        }
        TeamsNametagHandler.reloadPlayer(player);
        Bukkit.broadcastMessage(ColorText.translate(watcherNameStars + faction.getName() + " " + player.getName() + " &edisbanded the faction &c" + faction.getName()));

        TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_DISBAND_TEAM, ImmutableMap.of(
                "playerId", player.getUniqueId(),
                "playerName", player.getName()
        ));

        faction.disband();
    }

}