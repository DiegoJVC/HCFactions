package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class CmdForceDisbandAll {

    private static Runnable confirmRunnable;

    @Command(names={ "forcedisbandall" }, permission="hcfactions.forcedisbandall")
    public static void forceDisbandAll(CommandSender sender) {
        confirmRunnable = () -> {
            List<Faction> factions = new ArrayList<>();

            for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
                factions.add(faction);
            }

            for (Faction faction : factions) {
                faction.disband();
            }

            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All factions have been forcibly disbanded!");
        };

        sender.sendMessage(ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED + "/forcedisbandall confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN + "/forcedisbandall cancel" + ChatColor.RED +" to cancel.");
    }

    @Command(names = {"forcedisbandall confirm"}, permission = "hcfactions.forcedisbandall")
    public static void confirm(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "If you're sure...");
        confirmRunnable.run();
    }

    @Command(names = {"forcedisbandall cancel"}, permission = "hcfactions.forcedisbandall")
    public static void cancel(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Cancelled.");
        confirmRunnable = null;
    }

}