package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class GoppleCommand {

    @Command(names={ "Gapple" }, permission="")
    public static void gopple(Player sender) {
        if (HCFactions.getInstance().getOppleMap().isOnCooldown(sender.getUniqueId())) {
            Long then = HCFactions.getInstance().getOppleMap().getCooldown(sender.getUniqueId()) - System.currentTimeMillis();
            if (then != null) {
                DateTime now = DateTime.now();
                DateTime thendt = new DateTime(then);
                int minutes = 30 - Minutes.minutesBetween(thendt, now).getMinutes();
                if (minutes == 1) {
                    sender.sendMessage(ChatColor.RED + "You cannot consume another god apple for " + (60 - Seconds.secondsBetween(thendt, now).getSeconds()) + "  seconds.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You cannot consume another god apple for " + minutes + " minutes.");
                }
            } else {
                sender.sendMessage(ChatColor.GREEN + "You can consume another god apple!");
            }
        } else {
            sender.sendMessage(ChatColor.GREEN + "You can consume another god apple!");
        }
    }

}