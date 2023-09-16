package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class CmdKick {

    @Command(names = {"f kick", "faction kick", "fac kick"}, permission = "")
    public static void teamKick(Player sender, @Param(name = "player") UUID player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (!(faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
            return;
        }

        if (!faction.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't on your faction!");
            return;
        }

        if (faction.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the faction leader!");
            return;
        }

        if(faction.isCoLeader(player) && (!faction.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (faction.isCaptain(player) && !faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = HCFactions.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null && SpawnTagHandler.isTagged(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + bukkitPlayer.getName() + " is currently combat-tagged! You can forcibly kick " + bukkitPlayer.getName() + " by using '"
                    + ChatColor.YELLOW + "/f forcekick " + bukkitPlayer.getName() + ChatColor.RED + "' which will cost your faction 1 DTR.");
            return;
        }

        faction.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " was kicked by " + sender.getName() + "!");

        TeamActionTracker.logActionAsync(faction, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "false"
        ));

        if (faction.removeMember(player)) {
            faction.disband();
        } else {
            faction.flagForSave();
        }

        HCFactions.getInstance().getFactionHandler().setTeam(player, null);

        if (bukkitPlayer != null) {
            TeamsNametagHandler.reloadPlayer(bukkitPlayer);
            TeamsNametagHandler.reloadOthersFor(bukkitPlayer);
        }
    }

}