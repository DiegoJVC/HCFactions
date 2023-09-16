package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHActivateCommand {

    @Command(names={ "KOTH Activate" }, permission="hcfactions.activatekoth")
    public static void kothActivate(Player sender, @Param(name="event") Event koth) {
        for (Event otherKoth : HCFactions.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        if( (koth.getName().equalsIgnoreCase("citadel")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }

        koth.activate();
        sender.sendMessage(ChatColor.GREEN + "Activated " + koth.getName() + ".");
    }

}
