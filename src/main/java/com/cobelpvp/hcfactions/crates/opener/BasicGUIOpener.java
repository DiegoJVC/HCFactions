package com.cobelpvp.hcfactions.crates.opener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import com.cobelpvp.hcfactions.crates.Winning;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class BasicGUIOpener extends Opener implements Listener {

	private HCFactions HCFactions;
	private HashMap<UUID, Integer> tasks = new HashMap<>();
	private HashMap<UUID, Inventory> guis = new HashMap<>();
	private int length = 10;

	public BasicGUIOpener(HCFactions HCFactions) {
		super(HCFactions, "BasicGUI");
		this.HCFactions = HCFactions;
	}

	@Override
	public void doSetup() {
		FileConfiguration config = getOpenerConfig();
		if (!config.isSet("Length")) {
			config.set("Length", HCFactions.getConfigHandler().getCrateGUITime());
			try {
				config.save(getOpenerConfigFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		length = config.getInt("Length");
		HCFactions.getServer().getPluginManager().registerEvents(this, HCFactions);
	}

	@Override
	public void doOpen(final Player player, final Crate crate, Location blockLocation) {
		final Inventory winGUI;
		final Integer[] timer = {0};
		final Integer[] currentItem = new Integer[1];

		Random random = new Random();
		int max = crate.getWinnings().size() - 1;
		int min = 0;
		currentItem[0] = random.nextInt((max - min) + 1) + min;
		winGUI = Bukkit.createInventory(null, 45, crate.getColor() + crate.getName() + " Win");
		guis.put(player.getUniqueId(), winGUI);
		player.openInventory(winGUI);
		final int maxTimeTicks = length * 10;
		tasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimerAsynchronously(HCFactions, new BukkitRunnable() {
			public void run() {
				if (!player.isOnline()) {
					finish(player);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + player.getName() + " " + crate.getName() + " 1");
					Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
					return;
				}
				Integer i = 0;
				while (i < 45) {
					if (i == 22) {
						i++;
						if (crate.getWinnings().size() == currentItem[0])
							currentItem[0] = 0;
						final Winning winning;
						if (timer[0] == maxTimeTicks) {
							winning = getWinning(crate);
						} else {
							winning = crate.getWinnings().get(currentItem[0]);
						}

						final ItemStack currentItemStack = winning.getPreviewItemStack();
						if (timer[0] == maxTimeTicks) {
							winning.runWin(player);
						}
						winGUI.setItem(22, currentItemStack);

						currentItem[0]++;
						continue;
					}
					ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) HCFactions.getCrateHandler().randInt(0, 15));
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (timer[0] == maxTimeTicks) {
						itemMeta.setDisplayName(ChatColor.RESET + "Winner!");
					} else {
						Sound sound;
						try {
							sound = Sound.valueOf("NOTE_PIANO");
						} catch (Exception e) {
							try {
								sound = Sound.valueOf("BLOCK_NOTE_HARP");
							} catch (Exception ee) {
								return; // This should never happen!
							}
						}
						final Sound finalSound = sound;
						Bukkit.getScheduler().runTask(HCFactions, new Runnable() {
							@Override
							public void run() {
								if (player.getOpenInventory().getTitle() != null && player.getOpenInventory().getTitle().contains(" Win"))
									player.playSound(player.getLocation(), finalSound, (float) 0.2, 2);
							}
						});
						itemMeta.setDisplayName(ChatColor.RESET + "Rolling...");
					}
					itemStack.setItemMeta(itemMeta);
					winGUI.setItem(i, itemStack);
					i++;
				}
				if (timer[0] == maxTimeTicks) {
					finish(player);
					Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
					return;
				}
				timer[0]++;
			}
		}, 0L, 2L).getTaskId());
	}

	@Override
	public void doReopen(Player player, Crate crate, Location location) {
		player.openInventory(guis.get(player.getUniqueId()));
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle() != null && event.getInventory().getTitle().contains(" Win") && !event.getInventory().getTitle().contains("Edit ")) {
			if (event.getInventory().getType() != null && event.getInventory().getType() == InventoryType.CHEST && event.getSlot() != 22 || (event.getCurrentItem() != null)) {
				event.setCancelled(true);
				event.getWhoClicked().closeInventory();
			}
		}
	}

}
