package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.listener.EndListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class ToggleEndCommand {

    @Command(names={ "ToggleEnd" }, permission="hcfactions.toggleend")
    public static void toggleEnd(Player sender) {
        EndListener.endActive = !EndListener.endActive;
        sender.sendMessage(ChatColor.YELLOW + "End enabled? " + ChatColor.LIGHT_PURPLE + (EndListener.endActive ? "Yes" : "No"));
    }

}