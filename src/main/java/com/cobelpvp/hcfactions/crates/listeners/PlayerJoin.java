package com.cobelpvp.hcfactions.crates.listeners;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

	private HCFactions HCFactions;

	public PlayerJoin(HCFactions HCFactions) {
		this.HCFactions = HCFactions;
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskLater(HCFactions, new Runnable() {
			@Override
			public void run() {
				if (HCFactions.getCrateHandler().hasPendingKeys(event.getPlayer().getUniqueId())) {
					event.getPlayer().sendMessage(HCFactions.getMessageHandler().getMessage("Claim Join", event.getPlayer(), null, null));
				}
			}
		}, 1L);
	}

}
