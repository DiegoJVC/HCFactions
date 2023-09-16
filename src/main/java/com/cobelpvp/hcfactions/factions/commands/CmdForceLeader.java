package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdForceLeader {

    @Command(names={ "ForceLeader" }, permission="hcfactions.forceleader")
    public static void forceLeader(Player sender, @Param(name="player", defaultValue="self") UUID player) {
        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.GRAY + "That player is not on a faction.");
            return;
        }

        Player bukkitPlayer = Bukkit.getPlayer(player);

        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you leader of §b" + playerFaction.getName() + "§e.");
        }

        playerFaction.setOwner(player);
        sender.sendMessage(ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " is now the owner of " + ChatColor.RED + playerFaction.getName() + ChatColor.YELLOW + ".");
    }

}