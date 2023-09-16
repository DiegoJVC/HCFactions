package com.cobelpvp.hcfactions.factions.menu.button;

import lombok.AllArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DTRButton extends Button {

    Faction faction;
    boolean increase;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack it = new ItemStack(getMaterial(player));
        ItemMeta im = it.getItemMeta();

        im.setDisplayName(getName(player));
        im.setLore(getDescription(player));

        it.setDurability(getDamageValue(player));
        return null;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (!increase && (faction.getDTR() - 1) <= 0 && !player.hasPermission("hcfactions.dtr.setraidable")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set factions as raidable. This has been logged.");
            return;
        }

        if (increase && faction.getMaxDTR() <= faction.getDTR() + 1) {
            player.sendMessage(ChatColor.RED + "This would put the faction above their maximum DTR. This has been logged.");
            return;
        }

        if (increase) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);
            faction.setDTR(faction.getDTR() + 1, player);
        } else {
            faction.setDTR(faction.getDTR() - 1, player);
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20f, 0.1F);
        }
        player.closeInventory();

    }

    public String getName(Player player) {
        return increase ? "§aIncrease by 1.0" : "§cDecrease by 1.0";
    }

    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(Player player) {
        return increase ? (byte) 5 : (byte) 14;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }
}