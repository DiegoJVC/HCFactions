package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.util.RegenUtils;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlockRegenListener implements Listener {

    private static final Set<Material> REGEN = ImmutableSet.of(
            Material.COBBLESTONE,
            Material.DIRT,
            Material.WOOD,
            Material.NETHERRACK,
            Material.LEAVES,
            Material.LEAVES_2
    );

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelled() || HCFactions.getInstance().getServerHandler().isAdminOverride(player)) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if ((faction == null || !faction.isMember(event.getPlayer().getUniqueId())) && (player.getItemInHand() != null && REGEN.contains(player.getItemInHand().getType()))) {
            RegenUtils.schedule(event.getBlock(), 1, TimeUnit.HOURS, (block) -> {}, (block) -> {
                Faction currentFaction = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

                return !(currentFaction != null && currentFaction.isMember(player.getUniqueId()));
            });
        }
    }

}
