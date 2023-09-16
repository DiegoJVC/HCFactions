package com.cobelpvp.hcfactions.commands;

import java.text.NumberFormat;
import java.util.Collections;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.util.ExperienceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cobelpvp.atheneum.command.Command;

public final class BottleCommand implements Listener {

    public BottleCommand() {
        Bukkit.getPluginManager().registerEvents(this, HCFactions.getInstance());
    }
    
    @Command(names = {"bottle"}, permission = "hcfactions.bottle")
    public static void bottle(Player sender) {
        ItemStack item = sender.getItemInHand();

        if (item == null || item.getType() != Material.GLASS_BOTTLE || item.getAmount() != 1) {
            sender.sendMessage(ChatColor.RED + "You must be holding one glass bottle in your hand.");
            return;
        }

        ExperienceManager manager = new ExperienceManager(sender);
        int experience = manager.getCurrentExp();
        manager.setExp(0.0D);

        if (experience == 0) {
            sender.sendMessage(ChatColor.RED + "You don't have any experience to bottle!");
            return;
        }

        ItemStack result = new ItemStack(Material.EXP_BOTTLE);
        ItemMeta meta = result.getItemMeta();
        meta.setLore(Collections.singletonList(
                ChatColor.GREEN + "XP: " + ChatColor.GOLD + NumberFormat.getInstance().format(experience)
        ));
        result.setItemMeta(meta);

        sender.setItemInHand(result);
        sender.sendMessage(ChatColor.GREEN + "You have bottled " + NumberFormat.getInstance().format(experience) + " XP!");
        sender.playSound(sender.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    }

}