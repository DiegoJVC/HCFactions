package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class SetWorldBufferCommand {

    @Command(names={ "SetWorldBuffer" }, permission="op")
    public static void setWorldBuffer(Player sender, @Param(name="worldBuffer") int newBuffer) {
        HCFactions.getInstance().getMapHandler().setWorldBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The world buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                HCFactions.getInstance().getMapHandler().saveWorldBuffer();
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

}
