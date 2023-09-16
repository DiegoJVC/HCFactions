package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddBalanceCommand {

    @Command(names={ "eco give" }, permission="hcfactions.addbalance")
    public static void addBal(CommandSender sender, @Param(name="player") UUID player, @Param(name="amount") float amount) {
        if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot add a balance this high. This action has been logged.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }


        if (amount > 250000 && sender instanceof Player) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        Player targetPlayer = HCFactions.getInstance().getServer().getPlayer(player);
        TeamsEconomyHandler.setBalance(player, TeamsEconomyHandler.getBalance(player) + amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + player + "§6 set to §e$" + TeamsEconomyHandler.getBalance(player));
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + TeamsEconomyHandler.getBalance(player) + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + TeamsEconomyHandler.getBalance(player) + "§a by §4CONSOLE§a.");
        }

        HCFactions.getInstance().getWrappedBalanceMap().setBalance(player, TeamsEconomyHandler.getBalance(player));
    }
}
