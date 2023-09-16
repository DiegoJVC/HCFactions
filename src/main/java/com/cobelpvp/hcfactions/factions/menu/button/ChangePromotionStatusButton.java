package com.cobelpvp.hcfactions.factions.menu.button;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.menu.ConfirmMenu;
import org.bukkit.Bukkit;
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
public class ChangePromotionStatusButton extends Button {

    @NonNull
    private UUID uuid;
    @NonNull
    private Faction faction;
    @NonNull
    private boolean promote;

    public String getName(Player player) {
        return promote ? "§aPromote §e" + UUIDUtils.name(uuid) : "§cDemote §e" + UUIDUtils.name(uuid);
    }

    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(promote ? "§eClick to promote §b" + UUIDUtils.name(uuid) + "§e to captain" : "§eClick to demote §b" + UUIDUtils.name(uuid) + "§e to member");
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
        if (promote) {
            String newRank;
            if (faction.isCaptain(uuid)) {
                newRank = "co-leader";
            } else {
                newRank = "captain";
            }
            new ConfirmMenu("Make " + UUIDUtils.name(uuid) + " " + newRank + "?", (b) -> {
                if (b) {
                    if (faction.isCaptain(uuid)) {
                        faction.removeCaptain(uuid);
                        faction.addCoLeader(uuid);
                    } else {
                        faction.addCaptain(uuid);
                    }
                    Player bukkitPlayer = Bukkit.getPlayer(uuid);

                    if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                        bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you a §a" + newRank + " §eof your faction.");
                    }

                    player.sendMessage(ChatColor.YELLOW + UUIDUtils.name(uuid) + " has been made a " + newRank + " of " + faction.getName() + ".");
                }
            }).openMenu(player);
        } else {
            new ConfirmMenu("Make " + UUIDUtils.name(uuid) + " member?", (b) -> {
                if (b) {
                    faction.removeCaptain(uuid);
                    faction.removeCoLeader(uuid);

                    Player bukkitPlayer = Bukkit.getPlayer(uuid);

                    if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                        bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you a §bmember §eof your faction.");
                    }

                    player.sendMessage(ChatColor.YELLOW + UUIDUtils.name(uuid) + " has been made a member of " + faction.getName() + ".");
                }
            }).openMenu(player);
        }
    }
}
