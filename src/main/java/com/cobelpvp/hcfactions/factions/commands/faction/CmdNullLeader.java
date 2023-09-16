package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdNullLeader {

    @Command(names={ "f nullleader", "faction nullleader", "fac nullleader" }, permission="hcfactions.nullleader")
    public static void teamNullLeader(Player sender) {
        int nullLeaders = 0;

        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            if (faction.getOwner() == null) {
                nullLeaders++;
                sender.sendMessage(ChatColor.RED + "- " + faction.getName());
            }
        }

        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null factions found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null factions found.");
        }
    }

}