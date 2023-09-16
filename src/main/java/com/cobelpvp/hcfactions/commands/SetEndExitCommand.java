package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.listener.EndListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class SetEndExitCommand {

    @Command(names = {"setendexit"}, permission = "hcfactions.setendexit")
    public static void setendexit(Player sender) {
        Location previous = EndListener.getEndReturn();
        EndListener.setEndReturn(sender.getLocation());
        Location current = EndListener.getEndReturn();

        sender.sendMessage(ChatColor.GREEN + "End exit point has updated.");

        EndListener.saveEndReturn();
    }

}