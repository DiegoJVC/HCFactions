package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.ColorText;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class InvToCommand {

    @Command(names = {"invto"}, permission = "hcfactions.staff.invto")
    public static void invToCommand(CommandSender sender, @Param(name = "target") Player target) {
        PlayerInventory targetInventory = target.getInventory();
        PlayerInventory senderInventory = ((Player) sender).getInventory();
        targetInventory.setArmorContents(senderInventory.getArmorContents());
        targetInventory.setContents(senderInventory.getContents());
        sender.sendMessage(ColorText.translate("&6You gived your inventory to " + target.getName() + "."));
    }
}
