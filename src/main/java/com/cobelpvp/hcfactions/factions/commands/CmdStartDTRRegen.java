package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdStartDTRRegen {

    @Command(names={ "startdtrregen" }, permission="hcfactions.startdtrregen")
    public static void startDTRRegen(Player sender, @Param(name="team") Faction faction) {
        faction.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.RED + faction.getName() + ChatColor.GREEN + " is now regenerating DTR.");
    }

}