package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdTP {

    @Command(names = {"f tp", "faction tp", "fac tp"}, permission = "hcfactions.factiontp")
    public static void teamTP(Player sender, @Param(name = "team", defaultValue = "self") Faction faction) {
        if (faction.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(faction.getHQ());
        } else if (faction.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(faction.getClaims().get(0).getMaximumPoint().add(0, 100, 0));
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

}
