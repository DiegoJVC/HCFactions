package com.cobelpvp.hcfactions.factions.menu.button;

import lombok.AllArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdShadowMute;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MuteButton extends Button {

    private int minutes;
    private Faction faction;

    public void clicked(Player player, int i, ClickType clickType) {
        CmdShadowMute.teamShadowMute(player, faction, minutes);
    }

    public String getName(Player player) {
        return "§e" + minutes + "m mute";
    }

    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    public byte getDamageValue(Player player) {
        return (byte) 0;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack it = new ItemStack(getMaterial(player));
        ItemMeta im = it.getItemMeta();

        im.setLore(getDescription(player));
        im.setDisplayName(getName(player));
        it.setItemMeta(im);
        it.setAmount(minutes);

        return it;
    }

    public Material getMaterial(Player player) {
        return Material.CHEST;
    }
}
