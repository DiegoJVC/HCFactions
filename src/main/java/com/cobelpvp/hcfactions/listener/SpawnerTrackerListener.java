package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Claim;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.server.event.CrowbarSpawnerBreakEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerTrackerListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

			if (faction != null) {
				Claim claim = LandBoard.getInstance().getClaim(event.getBlockPlaced().getLocation());

				if (claim != null && faction.getClaims().contains(claim)) {
					faction.addSpawnersInClaim(1);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.MOB_SPAWNER) {
			Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

			if (faction != null) {
				Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

				if (claim != null && faction.getClaims().contains(claim)) {
					faction.removeSpawnersInClaim(1);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpawnerBreakEvent(CrowbarSpawnerBreakEvent event) {
		Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

		if (faction != null) {
			Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

			if (claim != null && faction.getClaims().contains(claim)) {
				faction.removeSpawnersInClaim(1);
			}
		}
	}

}
