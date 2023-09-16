package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CitadelSaveLoottableCommand {

    @Command(names={"citadel saveloottable"}, permission="hcfactions.citadel.admin")
    public static void citadelSaveLoottable(Player sender) {
        HCFactions.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                HCFactions.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        HCFactions.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Saved Citadel loot from your inventory.");
    }

}