package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHDistCommand {

    @Command(names={ "KOTH Dist" }, permission="hcfactions.koth.admin")
    public static void kothDist(Player sender, @Param(name="koth") Event koth, @Param(name="distance") int distance) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Can only set distance for KOTHs");
            return;
        }

        ((KOTH) koth).setCapDistance(distance);
        sender.sendMessage(ChatColor.GREEN + "Set max distance for the " + koth.getName() + " KOTH.");
    }

}