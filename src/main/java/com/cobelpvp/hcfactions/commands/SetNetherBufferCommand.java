package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class SetNetherBufferCommand {

    @Command(names={ "SetNetherBuffer" }, permission="op")
    public static void setNetherBuffer(Player sender, @Param(name="netherBuffer") int newBuffer) {
        HCFactions.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                HCFactions.getInstance().getMapHandler().saveNetherBuffer();
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

}
