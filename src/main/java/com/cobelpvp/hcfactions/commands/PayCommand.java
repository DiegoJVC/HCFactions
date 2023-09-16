package com.cobelpvp.hcfactions.commands;

import java.text.NumberFormat;
import java.util.UUID;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class PayCommand {

    @Command(names={ "Pay", "P2P" }, permission="")
    public static void pay(Player sender, @Param(name="player") UUID player, @Param(name="amount") float amount) {
        double balance = TeamsEconomyHandler.getBalance(sender.getUniqueId());
        Player bukkitPlayer = HCFactions.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        if (sender.equals(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        if (amount < 5) {
            sender.sendMessage(ChatColor.RED + "You must send at least $5!");
            return;
        }

        if (balance > 100000) {
            sender.sendMessage("§cYour balance is too high to send money. Please contact an admin to transfer money.");
            Bukkit.getLogger().severe("[ECONOMY] " + sender.getName() + " tried to send " + amount);
            return;
        }

        if (Double.isNaN(balance)) {
            sender.sendMessage("§cYou can't send money because your balance is empty.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (balance < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have $" + amount + "!");
            return;
        }

        TeamsEconomyHandler.deposit(player, amount);
        TeamsEconomyHandler.withdraw(sender.getUniqueId(), amount);
 
        HCFactions.getInstance().getWrappedBalanceMap().setBalance(player, TeamsEconomyHandler.getBalance(player));
        HCFactions.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), TeamsEconomyHandler.getBalance(sender.getUniqueId()));

        sender.sendMessage(ChatColor.GREEN + "You sent " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.GREEN + " to " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.GREEN + ".");

        bukkitPlayer.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.GREEN + " sent you " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.GREEN + ".");
    }

}