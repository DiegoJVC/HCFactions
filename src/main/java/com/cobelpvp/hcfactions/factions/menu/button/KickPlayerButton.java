package com.cobelpvp.hcfactions.factions.menu.button;

import com.cobelpvp.hcfactions.factions.commands.CmdForceKick;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class KickPlayerButton extends Button {

    @NonNull
    private UUID uuid;
    @NonNull
    private Faction faction;

    public String getName(Player player) {
        return "§cKick §e" + UUIDUtils.name(uuid);
    }

    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        if (faction.isOwner(uuid)) {
            lore.add("§e§lLeader");
        } else if (faction.isCoLeader(uuid)) {
            lore.add("§e§lCo-Leader");
        } else if (faction.isCaptain(uuid)) {
            lore.add("§aCaptain");
        } else {
            lore.add("§7Member");
        }

        lore.add("");
        lore.add("§eClick to kick §b" + UUIDUtils.name(uuid) + "§e from faction.");

        return lore;
    }

    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        CmdForceKick.forceKick(player, uuid);
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
