package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.VisualClaim;
import com.cobelpvp.hcfactions.factions.claims.VisualClaimType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class CmdResize {

    public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_AXE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oResizing Wand");
        meta.setLore(Arrays.asList(

                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect resize's corners",
                "",
                "§eRight Click §6Air",
                "§b- §fCancel current claim",
                "",
                "§9Shift §eLeft Click §6Block/Air",
                "§b- §fPurchase current claim"

        ));

        SELECTION_WAND.setItemMeta(meta);
    }

    //TODO: Remove permission node to deploy
    @Command(names={ "f resize", "faction resize", "fac resize" }, permission="op")
    public static void teamResize(final Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId())) {
            sender.getInventory().remove(SELECTION_WAND);

            if (faction.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not resize land while your faction is raidable!");
                return;
            }

            new BukkitRunnable() {

                public void run() {
                    sender.getInventory().addItem(SELECTION_WAND.clone());
                }

            }.runTaskLater(HCFactions.getInstance(), 1L);

            new VisualClaim(sender, VisualClaimType.RESIZE, false).draw(false);

            if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
        }
    }

}