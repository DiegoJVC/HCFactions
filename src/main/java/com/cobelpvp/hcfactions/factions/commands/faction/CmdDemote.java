package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdDemote {

    @Command(names={"f demote", "faction demote", "fac demote" }, permission="")
    public static void teamDemote(Player sender, @Param(name="player") UUID player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (!faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only faction co-leaders (and above) can do this.");
            return;
        }

        if (!faction.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_RED + UUIDUtils.name(player) + " is not on your faction.");
            return;
        }

        if (faction.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is the leader. To change leaders, the faction leader must use /faction leader <name>");
        } else if (faction.isCoLeader(player)) {
            if (faction.isOwner(sender.getUniqueId())) {
                faction.removeCoLeader(player);
                faction.addCaptain(player);
                faction.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " has been demoted to Captain!");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the faction leader can demote Co-Leaders.");
            }
        } else if (faction.isCaptain(player)) {
            faction.removeCaptain(player);
            faction.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " has been demoted to a member!");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is currently a member. To kick them, use /faction kick");
        }
    }

}