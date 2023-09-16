package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete" }, permission="hcfactions.koth.admin")
    public static void kothDelete(Player sender, @Param(name="koth") Event koth) {
        HCFactions.getInstance().getEventHandler().getEvents().remove(koth);
        HCFactions.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.RED + "Deleted KOTH " + koth.getName() + ".");
    }

}