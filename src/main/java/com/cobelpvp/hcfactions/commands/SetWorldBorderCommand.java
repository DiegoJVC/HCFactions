package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.listener.BorderListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class SetWorldBorderCommand {

    @Command(names={ "SetWorldBorder" }, permission="op")
    public static void setWorldBorder(Player sender, @Param(name="border") int border) {
        BorderListener.BORDER_SIZE = border;
        sender.sendMessage(ChatColor.GRAY + "The world border is now set to " + BorderListener.BORDER_SIZE + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                HCFactions.getInstance().getMapHandler().saveBorder();
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

}