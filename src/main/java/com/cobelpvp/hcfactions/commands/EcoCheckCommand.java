package com.cobelpvp.hcfactions.commands;

import java.util.Map;
import java.util.UUID;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.cobelpvp.atheneum.command.Command;

public class EcoCheckCommand {

    @Command(names={ "ecocheck" }, permission="hcfactions.ecocheck")
    public static void ecoCheck(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            if (isBad(faction.getBalance())) {
                sender.sendMessage(ChatColor.YELLOW + "Team: " + ChatColor.WHITE + faction.getName());
            }
        }

        try {
            Map<UUID, Double> balances = TeamsEconomyHandler.getBalances();

            for (Map.Entry<UUID, Double> balanceEntry  : balances.entrySet()) {
                if (isBad(balanceEntry.getValue())) {
                    sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + UUIDUtils.name(balanceEntry.getKey()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isBad(double bal) {
        return (Double.isNaN(bal) || Double.isInfinite(bal) || bal > 1_000_000D);
    }

}