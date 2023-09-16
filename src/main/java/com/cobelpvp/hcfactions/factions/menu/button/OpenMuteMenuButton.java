package com.cobelpvp.hcfactions.factions.menu.button;

import lombok.AllArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.hcfactions.commands.FactionManageCommand;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class OpenMuteMenuButton extends Button {

    private Faction faction;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        FactionManageCommand.muteTeam(player, faction);
    }

    public String getName(Player player) {
        return "§7Mute Faction";
    }

    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(Player player) {
        return 0;
    }

    public Material getMaterial(Player player) {
        return Material.CHEST;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack it = new ItemStack(getMaterial(player));
        ItemMeta im = it.getItemMeta();

        im.setDisplayName(getName(player));
        im.setLore(getDescription(player));

        it.setDurability(getDamageValue(player));
        it.setItemMeta(im);
        return it;
    }
}
