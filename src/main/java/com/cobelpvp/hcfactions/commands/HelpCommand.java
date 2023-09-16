package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand {

    @Command(names = "help")
    public static void helpCommand(Player player) {
        player.sendMessage(ChatColor.GOLD + "---[" + ChatColor.GREEN + "HCFactions Information" + ChatColor.GOLD +"]---");
        player.sendMessage(ChatColor.BLUE + "* " + ChatColor.RED +"HCFactions Map 1");
        player.sendMessage(ChatColor.BLUE + "* " + ChatColor.RED +"Map Started: 11/11/2022");
        player.sendMessage(ChatColor.GREEN + "End portals: " + ChatColor.GOLD +"1000 all quadrants");
        player.sendMessage(ChatColor.GREEN + "Glowstone Mountain: " + ChatColor.RED +"-500,0(Nether)");
        player.sendMessage(ChatColor.GREEN + "Map enchantments: " + ChatColor.GOLD + "Sharpness 2, Protection 2, Power 3");
        player.sendMessage(ChatColor.GREEN + "5 man per Faction" + ChatColor.RED + (" (Allies is not allowed)"));
        player.sendMessage(ChatColor.RED + "Type ./Rules for more information");
    }
}
