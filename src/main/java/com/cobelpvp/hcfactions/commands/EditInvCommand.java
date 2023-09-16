package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.entity.Player;

public class EditInvCommand {

    @Command(names = "editinv", permission = "hcfactions.editinventory")
    public static void editInventory(Player player, @Param(name ="target") Player target) {
        player.openInventory(target.getInventory());
        player.sendMessage("Editing inventory of " + target.getName() + ".");
    }
}
