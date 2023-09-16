package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.google.common.collect.ImmutableMap;

import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdDeposit {

    @Command(names={"f deposit", "faction deposit", "fac deposit", "f d", "faction d", "fac d", "f m d", "faction m d", "fac m d" }, permission="")
    public static void teamDeposit(Player sender, @Param(name="amount") float amount) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (amount <= 0 || Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "You can't deposit $0.0 (or less)!");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (TeamsEconomyHandler.getBalance(sender.getUniqueId()) < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
        }

        TeamsEconomyHandler.withdraw(sender.getUniqueId(), amount);

        sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " to the faction balance!");

        TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_DEPOSIT_MONEY, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName(),
                "amount", amount,
                "oldBalance", faction.getBalance(),
                "newBalance", faction.getBalance() + amount
        ));

        faction.setBalance(faction.getBalance() + amount);
        faction.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " into the faction balance.");

        HCFactions.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), TeamsEconomyHandler.getBalance(sender.getUniqueId()));
    }

    @Command(names={"f deposit all", "faction deposit all", "fac deposit all", "f d all", "faction d all", "fac d all", "f m d all", "faction m d all", "fac m d all" }, permission="")
    public static void teamDepositAll(Player sender) {
        teamDeposit(sender, (float) TeamsEconomyHandler.getBalance(sender.getUniqueId()));
    }

}