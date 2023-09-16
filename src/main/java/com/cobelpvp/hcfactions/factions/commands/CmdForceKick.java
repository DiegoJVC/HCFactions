package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdForceKick {

    @Command(names={ "forcekick" }, permission="hcfactions.forcekick")
    public static void forceKick(Player sender, @Param(name="player") UUID player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on a faction!");
            return;
        }

        if (faction.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + "'s faction has one member. Please use /forcedisband to perform this action.");
            return;
        }

        faction.removeMember(player);
        HCFactions.getInstance().getFactionHandler().setTeam(player, null);

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.RED + "You were kicked from your faction by a staff member.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + " from their faction, " + ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + ".");
    }

}