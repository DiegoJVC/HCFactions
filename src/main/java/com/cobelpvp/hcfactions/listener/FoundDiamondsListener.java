package com.cobelpvp.hcfactions.listener;

import java.util.Set;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.google.common.collect.ImmutableSet;

public class FoundDiamondsListener implements Listener {

    public static final Set<BlockFace> CHECK_FACES = ImmutableSet.of(
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST,
            BlockFace.UP,
            BlockFace.DOWN);

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.getBlock().setMetadata("DiamondPlaced", new FixedMetadataValue(HCFactions.getInstance(), true));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE && !event.getBlock().hasMetadata("DiamondPlaced")) {
            int diamonds = countRelative(event.getBlock());

            Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());
            if (playerFaction != null) {
                playerFaction.setDiamondsMined(playerFaction.getDiamondsMined() + diamonds);
            }

            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (HCFactions.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(player.getUniqueId())) {
                    player.sendMessage(ChatColor.AQUA+ "[Diamonds] " + ChatColor.GREEN + event.getPlayer().getName() + " found " + diamonds + " diamond" + (diamonds == 1 ? "" : "s") + ".");
                }
            }
        }
    }

    public int countRelative(Block block) {
        int diamonds = 1;
        block.setMetadata("DiamondPlaced", new FixedMetadataValue(HCFactions.getInstance(), true));

        for (BlockFace checkFace : CHECK_FACES) {
            Block relative = block.getRelative(checkFace);

            if (relative.getType() == Material.DIAMOND_ORE && !relative.hasMetadata("DiamondPlaced")) {
                relative.setMetadata("DiamondPlaced", new FixedMetadataValue(HCFactions.getInstance(), true));
                diamonds += countRelative(relative);
            }
        }

        return (diamonds);
    }

}