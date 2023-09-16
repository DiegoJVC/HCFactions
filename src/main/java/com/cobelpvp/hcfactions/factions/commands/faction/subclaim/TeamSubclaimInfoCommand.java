package com.cobelpvp.hcfactions.factions.commands.faction.subclaim;

import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimInfoCommand {

    @Command(names = {"f subclaim info", "faction subclaim info", "fac subclaim info", "f sub info", "faction sub info", "fac sub info"}, permission = "")
    public static void teamSubclaimInfo(Player sender, @Param(name = "subclaim", defaultValue = "location") Subclaim subclaim) {
        sender.sendMessage(ChatColor.BLUE + subclaim.getName() + ChatColor.YELLOW + " Subclaim Info");
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.GRAY + "Pos1. " + ChatColor.WHITE + subclaim.getLoc1().getBlockX() + "," + subclaim.getLoc1().getBlockY() + "," + subclaim.getLoc1().getBlockZ() + ChatColor.GRAY + " Pos2. " + ChatColor.WHITE + subclaim.getLoc2().getBlockX() + "," + subclaim.getLoc2().getBlockY() + "," + subclaim.getLoc2().getBlockZ());
        sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.WHITE + subclaim.getMembers().toString().replace("[", "").replace("]", ""));
    }

}