package com.cobelpvp.hcfactions.commands;

import java.util.List;
import java.util.UUID;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class LastInvCommand {

    @Command(names={ "lastinv" }, permission="hcfactions.lastinv")
    public static void lastInv(Player sender, @Param(name="player") UUID player) {
        HCFactions.getInstance().getServer().getScheduler().runTaskAsynchronously(HCFactions.getInstance(), () -> {
            Atheneum.getInstance().runRedisCommand((redis) -> {
                if (!redis.exists("lastInv:contents:" + player.toString())) {
                    sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + TeamsUUIDCache.name(player));
                    return null;
                }

                ItemStack[] contents = Atheneum.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + player.toString()), ItemStack[].class);
                ItemStack[] armor = Atheneum.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + player.toString()), ItemStack[].class);

                cleanLoot(contents);
                cleanLoot(armor);

                HCFactions.getInstance().getServer().getScheduler().runTask(HCFactions.getInstance(), () -> {
                    sender.getInventory().setContents(contents);
                    sender.getInventory().setArmorContents(armor);
                    sender.updateInventory();

                    sender.sendMessage(ChatColor.GREEN + "Loaded " + TeamsUUIDCache.name(player) + "'s last inventory.");
                });

                return null;
            });
        });
    }

    public static void cleanLoot(ItemStack[] stack) {
        for (ItemStack item : stack) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = item.getItemMeta().getLore();
                lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                meta.setLore(lore);

                item.setItemMeta(meta);
            }
        }
    }

    public static void recordInventory(Player player) {
        recordInventory(player.getUniqueId(), player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public static void recordInventory(UUID player, ItemStack[] contents, ItemStack[] armor) {
        HCFactions.getInstance().getServer().getScheduler().runTaskAsynchronously(HCFactions.getInstance(), () -> {
            Atheneum.getInstance().runRedisCommand((redis) -> {
                redis.set("lastInv:contents:" + player.toString(), Atheneum.PLAIN_GSON.toJson(contents));
                redis.set("lastInv:armorContents:" + player.toString(), Atheneum.PLAIN_GSON.toJson(armor));
                return null;
            });
        });
    }

}