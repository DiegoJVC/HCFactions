package com.cobelpvp.hcfactions.commands;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.engine.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class BalanceCommand {

    @Command(names={ "Balance", "Econ", "Eco", "Bal", "money" }, permission="")
    public static void balance(Player sender, @Param(name="player", defaultValue="self") UUID player) {
        if (sender.getUniqueId().equals(player)) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.DARK_GREEN + NumberFormat.getNumberInstance(Locale.US).format(TeamsEconomyHandler.getBalance(sender.getUniqueId())));
        } else {
            Profile profile = Profile.getByUsername(TeamsUUIDCache.name(player));
            String playerName = profile.getActiveRank().getGameColor() + TeamsUUIDCache.name(player);
            sender.sendMessage(ChatColor.GOLD + playerName + ChatColor.GOLD + "'s Balance" + ": " + ChatColor.DARK_GREEN + NumberFormat.getNumberInstance(Locale.US).format(TeamsEconomyHandler.getBalance(player)));
        }
    }

}