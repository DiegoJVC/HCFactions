package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;

public class KOTHHiddenCommand {

    @Command(names={ "KOTH Hidden" }, permission="hcfactions.koth.admin")
    public static void kothHidden(Player sender, @Param(name="koth") Event koth, @Param(name="hidden") boolean hidden) {
        koth.setHidden(hidden);
        sender.sendMessage(ChatColor.BLUE + "Set visibility for the " + koth.getName() + " event.");
    }

}