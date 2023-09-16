package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHLocCommand {

    @Command(names={ "KOTH loc" }, permission="hcfactions.koth.admin")
    public static void kothLoc(Player sender, @Param(name="koth") Event koth) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to set location for a non-KOTH event.");
        } else {
            ((KOTH) koth).setLocation(sender.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Set cap location for the " + koth.getName() + " KOTH.");
        }
    }

}