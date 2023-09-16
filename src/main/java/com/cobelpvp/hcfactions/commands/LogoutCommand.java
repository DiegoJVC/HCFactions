package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.server.ServerHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class LogoutCommand {

    @Command(names={ "Logout" }, permission="")
    public static void logout(Player sender) {
        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.DARK_RED + "You can't log out while you're frozen!");
            return;
        }

        if(ServerHandler.getTasks().containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out.");
            return;
        }

        HCFactions.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}