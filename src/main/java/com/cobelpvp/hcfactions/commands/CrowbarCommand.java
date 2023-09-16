package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.util.InventoryUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrowbarCommand {

    @Command(names={ "crowbar" }, permission="hcfactions.crowbar")
    public static void crowbar(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.DARK_RED + "This command must be ran in creative.");
            return;
        }

        sender.setItemInHand(InventoryUtils.CROWBAR);
        sender.sendMessage(ChatColor.GREEN + "Gave you a crowbar.");
    }

    @Command(names={ "crowbar" }, permission="hcfactions.crowbar")
    public static void crowbar(CommandSender sender, @Param(name = "player") Player target) {
        target.getInventory().addItem(InventoryUtils.CROWBAR);
        target.sendMessage(ChatColor.GREEN + "You received a crowbar from " + sender.getName() + ".");
        sender.sendMessage(ChatColor.GREEN + "You gave a crowbar to " + target.getName() + ".");
    }

}