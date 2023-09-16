package com.cobelpvp.hcfactions.events.systemfactions.destroythecore.commands;

import com.cobelpvp.hcfactions.events.systemfactions.destroythecore.DTC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class DTCCreateCommand {

    @Command(names={ "DTC Create" }, permission="hcfactions.dtc.admin")
    public static void kothCreate(Player sender, @Param(name="dtc") String koth) {
        new DTC(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + koth + ".");
    }

}
