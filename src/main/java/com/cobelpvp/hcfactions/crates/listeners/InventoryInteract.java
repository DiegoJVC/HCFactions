package com.cobelpvp.hcfactions.crates.listeners;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryInteract implements Listener {

	private HCFactions HCFactions;

	public InventoryInteract(HCFactions HCFactions) {
		this.HCFactions = HCFactions;
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null || (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains("Edit ")))
			return;
		if (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains(" " + HCFactions.getMessagesConfig().getString("Possible Wins Title"))) {
			event.setCancelled(true);
		} else if (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains("Claim Crate Keys")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				ItemStack itemStack = event.getCurrentItem();
				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().hasLore()) {
					// We assume it's a key
					HashMap<String, Integer> keys = HCFactions.getCrateHandler().getPendingKey(event.getWhoClicked().getUniqueId());
					Object[] keyNames = keys.keySet().toArray();
					if (event.getSlot() >= keyNames.length)
						return;
					String keyName = (String) keyNames[event.getSlot()];
					if (keyName != null) {
						HCFactions.getCrateHandler().claimKey(event.getWhoClicked().getUniqueId(), keyName);
						if (HCFactions.getCrateHandler().hasPendingKeys(event.getWhoClicked().getUniqueId()))
							((Player) event.getWhoClicked()).performCommand("crate claim");
						else
							event.getWhoClicked().closeInventory();
					}
				}
			}
		}
	}

}
