package com.cobelpvp.hcfactions.reclaims.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class RemoveReclaim {

    @Command(names = { "removereclaim" }, description = "Reset a player's reclaim", permission = "op")
    public static void reset(CommandSender sender, @Param(name = "target") UUID target) {
        if (HCFactions.getInstance().getReclaimHandler().getHasReclaimed().remove(target)) {
            sender.sendMessage(ChatColor.GREEN + "Reset " + TeamsUUIDCache.name(target) + "'s reclaim!");
        } else {
            sender.sendMessage(ChatColor.RED + TeamsUUIDCache.name(target) + " hasn't claimed their reclaim!");
        }
    }

}
