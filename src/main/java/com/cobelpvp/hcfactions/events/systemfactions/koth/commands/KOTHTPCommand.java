package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHTPCommand {

    @Command(names={ "KOTH TP", "KOTHTP" }, permission="hcfactions.koth")
    public static void kothTP(Player sender, @Param(name="koth", defaultValue="active") Event koth) {
        if (koth.getType() == EventType.KOTH) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(HCFactions.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GREEN + "Teleported to the " + koth.getName() + " KOTH.");
        } else if (koth.getType() == EventType.DTC) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(HCFactions.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GREEN + "Teleported to the " + koth.getName() + " DTC.");
        }

        sender.sendMessage(ChatColor.RED + "You can't TP to an event that doesn't have a location.");
    }

}