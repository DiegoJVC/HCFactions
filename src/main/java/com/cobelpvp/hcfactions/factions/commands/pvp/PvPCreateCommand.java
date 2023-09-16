package com.cobelpvp.hcfactions.factions.commands.pvp;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PvPCreateCommand {

    @Command(names = {"pvptimer create", "pvp create"}, permission = "worldedit.*")
    public static void pvpCreate(Player sender, @Param(name = "player", defaultValue = "self") Player player) {
        HCFactions.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
        player.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

        if (sender != player) {
            sender.sendMessage(ChatColor.YELLOW + "Gave 30 minutes of PVP Timer to " + player.getName() + ".");
        }
    }

}