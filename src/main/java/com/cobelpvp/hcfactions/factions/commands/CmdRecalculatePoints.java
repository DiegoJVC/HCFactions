package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdRecalculatePoints {
    
    @Command(names = {"faction recalculatepoints", "f recalculatepoints", "f recalcpoints"}, permission = "hcfactions.recalculatepoints")
    public static void recalculate(CommandSender sender) {
        int changed = 0;
        
        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            int oldPoints = faction.getPoints();
            faction.recalculatePoints();
            if (faction.getPoints() != oldPoints) {
                faction.flagForSave();
                sender.sendMessage(ChatColor.YELLOW + "Changed " + faction.getName() + "'s points from " + oldPoints + " to " + faction.getPoints());
                changed++;
            }

        }
        
        sender.sendMessage(ChatColor.YELLOW + "Changed a total of " + changed + " factions points.");
    }
    
}
