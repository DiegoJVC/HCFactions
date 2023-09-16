package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class RegenCommand {

    @Command(names={ "Regen", "DTR" }, permission="")
    public static void regen(Player sender, @Param(name="team", defaultValue="self") Faction faction) {
        if (!sender.isOp()) {
            faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);
        }

        if (faction == null) {
            sender.sendMessage(ChatColor.DARK_RED + "You are not on a faction!");
            return;
        }

        if (faction.getMaxDTR() == faction.getDTR()) {
            sender.sendMessage(ChatColor.GREEN + "Your faction is currently at max DTR, which is " + ChatColor.LIGHT_PURPLE + faction.getMaxDTR() + ChatColor.GREEN + ".");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Your faction has a max DTR of " + ChatColor.LIGHT_PURPLE + faction.getMaxDTR() + ChatColor.GREEN + ".");
        sender.sendMessage(ChatColor.GREEN + "You are regaining DTR at a rate of " + ChatColor.LIGHT_PURPLE + Faction.DTR_FORMAT.format(faction.getDTRIncrement() * 60) + "/hour" + ChatColor.GREEN + ".");
        sender.sendMessage(ChatColor.GREEN + "At this rate, it will take you " + ChatColor.LIGHT_PURPLE + (hrsToRegain(faction) == -1 ? "Infinity" : hrsToRegain(faction)) + ChatColor.GREEN + " hours to fully gain all DTR.");

        if (faction.getDTRCooldown() > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.GREEN + "Your faction is on DTR cooldown for " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString((int) (faction.getDTRCooldown() - System.currentTimeMillis()) / 1000) + ChatColor.GREEN + ".");
        }
    }

    private static double hrsToRegain(Faction faction) {
        double diff = faction.getMaxDTR() - faction.getDTR();
        double dtrIncrement = faction.getDTRIncrement();

        if (dtrIncrement == 0D) {
            return (-1);
        }

        double required = diff / dtrIncrement;
        double h = required / 60D;

        return (Math.round(10.0 * h) / 10.0);
    }

}