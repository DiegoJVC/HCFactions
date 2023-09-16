package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CitadelLoadLoottableCommand {

    @Command(names={"citadel loadloottable"}, permission="hcfactions.citadel.admin")
    public static void citadelLoadLoottable(Player sender) {
        sender.getInventory().clear();
        int itemIndex = 0;

        for (ItemStack itemStack : HCFactions.getInstance().getCitadelHandler().getCitadelLoot()) {
            sender.getInventory().setItem(itemIndex, itemStack);
            itemIndex++;
        }

        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded Citadel loot into your inventory.");
    }

}