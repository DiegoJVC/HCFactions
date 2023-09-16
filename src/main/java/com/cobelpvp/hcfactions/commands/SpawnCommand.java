package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class SpawnCommand {

    @Command(names={ "spawn" }, permission="")
    public static void spawn(Player sender) {
        if (sender.hasPermission("hcfactions.spawn")) {
            sender.teleport(HCFactions.getInstance().getServerHandler().getSpawnLocation());
        } else {
            sender.sendMessage(ChatColor.RED + "");
            sender.sendMessage(ChatColor.GREEN + "Spawn location: 0, 0.");
            sender.sendMessage(ChatColor.RED + "");
        }
    }

}
