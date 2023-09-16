package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdMaxOnline {

    @Command(names={ "faction maxOnline" }, permission="hcfactions.maxonline")
    public static void teamMaxOnline(Player sender, @Param(name="team") Faction faction, @Param(name="max online", defaultValue="-5") int maxOnline) {
        if (maxOnline == -5) {
            if (faction.getMaxOnline() == -1) {
                sender.sendMessage(faction.getName(sender) + ChatColor.YELLOW + "'s player limit is " + ChatColor.GREEN + "not set" + ChatColor.YELLOW + ".");
            } else {
                sender.sendMessage(faction.getName(sender) + ChatColor.YELLOW + "'s player limit is " + ChatColor.RED + faction.getMaxOnline() + ChatColor.YELLOW + ".");
            }
        } else {
            faction.setMaxOnline(maxOnline);
            sender.sendMessage(faction.getName(sender) + ChatColor.YELLOW + "'s player limit has been set to " + ChatColor.RED + maxOnline + ChatColor.YELLOW + ".");
        }
    }

}