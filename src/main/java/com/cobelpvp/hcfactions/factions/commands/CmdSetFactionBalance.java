package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdSetFactionBalance {

    @Command(names={ "setfactionbalance", "setfactionbal" }, permission="hcfactions.setfactionbalance")
    public static void setTeamBalance(Player sender, @Param(name="team") Faction faction, @Param(name="balance") float balance) {
        faction.setBalance(balance);
        sender.sendMessage(ChatColor.GREEN + faction.getName() + ChatColor.GREEN + "'s balance is now " + ChatColor.GOLD + faction.getBalance() + ChatColor.GREEN + ".");
    }

}