package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHTimeCommand {

    @Command(names={ "KOTH Time" }, permission="hcfactions.koth.admin")
    public static void kothTime(Player sender, @Param(name="koth") Event koth, @Param(name="time") float time) {
        if (time > 20F) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "To set a KOTH's capture time to 20 minutes 30 seconds, use /koth time 20.5");
        }

        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to modify cap time for a non-KOTH event.");
        } else {
            ((KOTH) koth).setCapTime((int) (time * 60F));
            sender.sendMessage(ChatColor.GREEN + "Set cap time for the " + koth.getName() + " KOTH.");
        }
    }

}