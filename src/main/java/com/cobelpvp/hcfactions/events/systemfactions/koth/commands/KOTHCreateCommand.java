package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class KOTHCreateCommand {

    @Command(names={ "KOTH Create" }, permission="hcfactions.koth.admin")
    public static void kothCreate(Player sender, @Param(name="koth") String koth) {
        new KOTH(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GREEN + "KOTH " + koth + " created.");
    }

}