package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdWithdraw {

    @Command(names={"f withdraw", "faction withdraw", "fac withdraw", "f w", "faction w", "fac w"}, permission="")
    public static void teamWithdraw(Player sender, @Param(name = "amount") float amount) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (faction.isCaptain(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isOwner(sender.getUniqueId())) {
            if (faction.getBalance() < amount) {
                sender.sendMessage(ChatColor.RED + "The faction doesn't have enough money to do this!");
                return;
            }

            if (Double.isNaN(faction.getBalance())) {
                sender.sendMessage(ChatColor.RED + "You cannot withdraw money because your faction's balance is broken!");
                return;
            }

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "You can't withdraw $0.0 (or less)!");
                return;
            }

            if (amount == Float.NaN) {
                sender.sendMessage(ChatColor.RED + "Nope.");
                return;
            }

            TeamsEconomyHandler.deposit(sender.getUniqueId(), amount);
            sender.sendMessage(ChatColor.YELLOW + "You have withdrawn " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " from the faction balance!");

            TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_WITHDRAW_MONEY, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "amount", amount,
                    "oldBalance", faction.getBalance(),
                    "newBalance", faction.getBalance() - amount
            ));

            faction.setBalance(faction.getBalance() - amount);
            faction.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.LIGHT_PURPLE + "$" + amount + ChatColor.YELLOW + " from the faction balance.");
        } else {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
        }
    }

}