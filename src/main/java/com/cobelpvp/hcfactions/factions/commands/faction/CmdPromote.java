package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdPromote {

    @Command(names={ "f promote", "faction promote", "fac promote" }, permission="")
    public static void teamPromote(Player sender, @Param(name="player") UUID player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (!faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only faction co-leaders (and above) can do this.");
            return;
        }

        if (!faction.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on your faction.");
            return;
        }

        if (faction.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a leader.");
        } else if (faction.isCoLeader(player)) {
            if (faction.isOwner(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a co-leader! To make them a leader, use /t leader");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the faction leader can promote new leaders.");
            }
        } else if (faction.isCaptain(player)) {
            if (faction.isOwner(sender.getUniqueId())) {
                faction.sendMessage(ChatColor.GOLD + UUIDUtils.name(player) + " has been promoted to Co-Leader!");
                faction.addCoLeader(player);
                faction.removeCaptain(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Only the faction leader can promote new Co-Leaders.");
            }
        } else {
            faction.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been promoted to Captain!");
            faction.addCaptain(player);
        }
    }

}