package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class LocationCommand {

    @Command(names={ "Location", "Loc" }, permission="")
    public static void location(Player sender) {
        Location loc = sender.getLocation();
        Faction owner = LandBoard.getInstance().getTeam(loc);

        if (owner != null) {
            sender.sendMessage(ChatColor.YELLOW + "You are in " + owner.getName(sender.getPlayer()) + ChatColor.YELLOW + "'s territory.");
            return;
        }

        if (!HCFactions.getInstance().getServerHandler().isWarzone(loc)) {
            sender.sendMessage(ChatColor.YELLOW + "You are in " + ChatColor.GREEN + "Wilderness" + ChatColor.YELLOW + "!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "You are in the " + ChatColor.RED + "Warzone" + ChatColor.YELLOW + "!");
        }
    }

}