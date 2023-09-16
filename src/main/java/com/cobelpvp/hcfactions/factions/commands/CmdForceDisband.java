package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdForceDisband {

    @Command(names={ "forcedisband" }, permission="hcfactions.factions.forcedisband")
    public static void forceDisband(Player sender, @Param(name="team") Faction faction) {
        faction.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the faction.");
        faction.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the faction " + ChatColor.RED + faction.getName() + ChatColor.YELLOW + ".");
    }

}