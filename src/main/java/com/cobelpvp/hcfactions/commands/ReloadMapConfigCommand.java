package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class ReloadMapConfigCommand {

    @Command(names={ "reloadMapConfig" }, permission="op")
    public static void reloadMapConfig(Player sender) {
        HCFactions.getInstance().getMapHandler().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Reloaded mapInfo.json from file.");
    }

}