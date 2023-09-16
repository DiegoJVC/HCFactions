package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate" }, permission="hcfactions.koth.admin")
    public static void kothDectivate(CommandSender sender, @Param(name="koth") Event koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.RED + koth.getName() + " is now inactive.");
    }

}
