package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdHome {

    @Command(names = {"f hq", "faction hq", "fac hq", "f home", "faction home", "fac home", "home", "hq"}, permission = "")
    public static void teamHQ(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a faction!");
            return;
        }

        if (faction.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (HCFactions.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction headquarters during the End of the World!");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction headquarters while you're frozen!");
            return;
        }

        if (HCFactions.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            return;
        }

        boolean charge = faction != LandBoard.getInstance().getTeam(sender.getLocation()) && !HCFactions.getInstance().getConfig().getBoolean("oldhcf");

        HCFactions.getInstance().getServerHandler().beginHQWarp(sender, faction, 10, charge);
    }
}