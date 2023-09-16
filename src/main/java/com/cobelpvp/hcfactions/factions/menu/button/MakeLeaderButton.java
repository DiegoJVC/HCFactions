package com.cobelpvp.hcfactions.factions.menu.button;

import com.cobelpvp.hcfactions.factions.commands.CmdForceLeader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.menu.ConfirmMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MakeLeaderButton extends Button {

    @NonNull
    private UUID uuid;
    @NonNull
    private Faction faction;

    public String getName(Player player) {
        return (faction.isOwner(uuid) ? "§a§l" : "§7") + UUIDUtils.name(uuid);
    }

    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        if (faction.isOwner(uuid)) {
            lore.add("§aThis player is already the leader!");
        } else {
            lore.add("§eClick to change §b" + faction.getName() + "§b's§e leader");
            lore.add("§eto §6" + UUIDUtils.name(uuid));
        }

        return lore;
    }

    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
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

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (faction.isOwner(uuid)) {
            player.sendMessage(ChatColor.RED + "That player is already the leader!");
            return;
        }

        new ConfirmMenu("Make " + UUIDUtils.name(uuid) + " leader?", (b) -> {
            if (b) {
                CmdForceLeader.forceLeader(player, uuid);

            }
        }).openMenu(player);


    }


}
