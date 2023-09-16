package com.cobelpvp.hcfactions.commands;

import java.util.UUID;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.persist.maps.PlaytimeMap;
import com.cobelpvp.atheneum.util.TimeUtils;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class PlaytimeCommand {

    @Command(names={ "Playtime", "PTime" }, permission="")
    public static void playtime(Player sender, @Param(name="player", defaultValue="self") UUID player) {
        PlaytimeMap playtime = HCFactions.getInstance().getPlaytimeMap();
        int playtimeTime = (int) playtime.getPlaytime(player);
        Player bukkitPlayer = HCFactions.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null && sender.canSee(bukkitPlayer)) {
            playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
        }

        sender.sendMessage(ChatColor.GREEN + "The playtime of " + UUIDUtils.name(player) + ChatColor.GREEN + " is: " + ChatColor.GOLD + TimeUtils.formatIntoDetailedString(playtimeTime));
    }

}