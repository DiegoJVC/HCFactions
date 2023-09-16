package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdForceJoin {

    @Command(names={ "ForceJoin" }, permission="hcfactions.forcejoin")
    public static void forceJoin(Player sender, @Param(name="team") Faction faction, @Param(name="player", defaultValue="self") Player player) {
        if (HCFactions.getInstance().getFactionHandler().getTeam(player) != null) {
            if (player == sender) {
                sender.sendMessage(ChatColor.RED + "Leave your current faction before attempting to forcejoin.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player needs to leave their current faction first!");
            }

            return;
        }

        faction.addMember(player.getUniqueId());
        HCFactions.getInstance().getFactionHandler().setTeam(player.getUniqueId(), faction);
        player.sendMessage(ChatColor.GREEN + "You are now a member of " + ChatColor.RED + faction.getName() + ChatColor.GREEN + "!");

        if (player != sender) {
            sender.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " added to " + ChatColor.RED + faction.getName() + ChatColor.YELLOW + "!");
        }
    }

}