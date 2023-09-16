package com.cobelpvp.hcfactions.crates;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.handlers.ConfigHandler;
import com.cobelpvp.hcfactions.crates.utils.EnchantmentUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Winning {

	private HCFactions HCFactions;
	private Crate crate;
	private boolean valid = false;
	private boolean command = false;
	private double percentage = 0;
	private ItemStack previewItemStack;
	private ItemStack winningItemStack;
	private List<String> commands = new ArrayList<>();
	private List<String> lore = new ArrayList<>();
	private String entityType = "";

	public Winning(Crate crate, String path, HCFactions HCFactions, ConfigHandler configHandler) {
		this.HCFactions = HCFactions;
		this.crate = crate;

		if (configHandler.isDebugMode()) {
			HCFactions.getLogger().info("Loading data for \"" + path + "\"");
		}

		FileConfiguration config = HCFactions.getConfig();
		if (!config.isSet(path))
			return;

		if (!config.isSet(path + ".Type"))
			return;
		String type = config.getString(path + ".Type");
		ItemStack itemStack;
		if (type.equalsIgnoreCase("item") || type.equalsIgnoreCase("block")) {
			Material itemType = null;
			if (config.isSet(path + ".Item Type"))
				itemType = Material.getMaterial(config.getString(path + ".Item Type").toUpperCase());
			else if (config.isSet(path + ".Block Type"))
				itemType = Material.getMaterial(config.getString(path + ".Block Type").toUpperCase());
			else if (config.isSet(path + ".Item ID"))
				itemType = Material.getMaterial(config.getInt(path + ".Item ID"));

			if (itemType == null)
				return;

			Integer itemData = 0;
			if (config.isSet(path + ".Item Data"))
				itemData = config.getInt(path + ".Item Data");

			if (config.isSet(path + ".Entity Type"))
				entityType = config.getString(path + ".Entity Type");

			if (config.isSet(path + ".Percentage"))
				percentage = config.getDouble(path + ".Percentage");

			Integer amount = 1;
			if (config.isSet(path + ".Amount"))
				amount = config.getInt(path + ".Amount");

			if (!entityType.isEmpty() && itemType == Material.MONSTER_EGG) {
				itemStack = HCFactions.getVersion_util().getSpawnEgg(EntityType.valueOf(entityType.toUpperCase()), amount);
			} else
				itemStack = new ItemStack(itemType, amount, Short.parseShort(String.valueOf(itemData)));
		} else if (type.equalsIgnoreCase("command")) {
			command = true;
			if (config.isSet(path + ".Commands") && config.getStringList(path + ".Commands").size() != 0) {
				commands = config.getStringList(path + ".Commands");
			} else if (config.isSet(path + ".commands") && config.getStringList(path + ".commands").size() != 0) {
				commands = config.getStringList(path + ".commands");
			}

			if (commands.isEmpty()) {
				HCFactions.getLogger().warning("No \"Commands\" found for " + path);
				return;
			}


			Material itemType = Material.PAPER;
			if (config.isSet(path + ".Item Type"))
				itemType = Material.getMaterial(config.getString(path + ".Item Type"));

			if (itemType == null)
				return;

			Integer itemData = 0;
			if (config.isSet(path + ".Item Data"))
				itemData = config.getInt(path + ".Item Data");

			if (config.isSet(path + ".Percentage"))
				percentage = config.getDouble(path + ".Percentage");

			Integer amount = 1;
			if (config.isSet(path + ".Amount"))
				amount = config.getInt(path + ".Amount");

			itemStack = new ItemStack(itemType, amount, Short.parseShort(String.valueOf(itemData)));
		} else {
			return;
		}
		ItemStack winningItemStack = itemStack.clone();
		ItemStack previewItemStack = itemStack.clone();

		boolean showAmountInTitle = false;
		int originalAmount = 0;
		if (previewItemStack.getAmount() > previewItemStack.getMaxStackSize()) { // Stop multiple stacks for the same item!
			originalAmount = previewItemStack.getAmount();
			showAmountInTitle = true;
			previewItemStack.setAmount(previewItemStack.getMaxStackSize());
		}

		ItemMeta previewItemStackItemMeta = previewItemStack.getItemMeta();
		String displayName = "";
		if (config.isSet(path + ".Name") && !config.getString(path + ".Name").equals("NONE"))
			displayName = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".Name"));
		if (showAmountInTitle)
			displayName = displayName + " x" + originalAmount;
		if (!displayName.equals(""))
			previewItemStackItemMeta.setDisplayName(displayName);
		previewItemStack.setItemMeta(previewItemStackItemMeta);

		if (config.isSet(path + ".Enchantments")) {
			List<?> enchtantments = config.getList(path + ".Enchantments");
			for (Object object : enchtantments) {
				String enchantment = (String) object;
				String[] args = enchantment.split("-");
				try {
					Integer level = 1;
					if (args.length > 1)
						level = Integer.valueOf(args[1]);
					previewItemStack.addUnsafeEnchantment(Enchantment.getByName(args[0].toUpperCase()), level);
				} catch (Exception ignored) {
				}
			}
		}

		if (config.isSet(path + ".Lore")) {
			List<String> lines = config.getStringList(path + ".Lore");
			for (String line : lines) {
				this.lore.add(ChatColor.translateAlternateColorCodes('&', line));
			}
		}


		ItemMeta winningItemStackItemMeta = winningItemStack.getItemMeta();
		displayName = "";
		if (config.isSet(path + ".Name") && !config.getString(path + ".Name").equals("NONE"))
			displayName = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".Name"));
		if (!displayName.equals(""))
			winningItemStackItemMeta.setDisplayName(displayName);
		winningItemStackItemMeta.setLore(this.lore);
		winningItemStack.setItemMeta(winningItemStackItemMeta);

		if (config.isSet(path + ".Enchantments")) {
			List<?> enchtantments = config.getList(path + ".Enchantments");
			for (Object object : enchtantments) {
				String enchantment = (String) object;
				String[] args = enchantment.split("-");
				Integer level = 1;
				if (args.length > 1)
					level = Integer.valueOf(args[1]);
				Enchantment enchantment1 = EnchantmentUtil.getEnchantmentFromNiceName(args[0].toUpperCase());
				if (enchantment1 == null)
					Bukkit.getLogger().warning("Invalid enchantment \"" + args[0].toUpperCase() + "\" found for item \"" + ChatColor.stripColor(displayName) + "\"");
				else
					winningItemStack.addUnsafeEnchantment(enchantment1, level);
			}
		}
		this.winningItemStack = winningItemStack;

		previewItemStackItemMeta = previewItemStack.getItemMeta();
		List<String> lore = new ArrayList<>(this.lore);
		if (percentage > 0 && !crate.isHidePercentages()) {
			// Percentage
			lore.add(ChatColor.LIGHT_PURPLE + "");
			lore.add(HCFactions.getMessageHandler().getMessage("Chance Message", null, crate, this));
		}
		previewItemStackItemMeta.setLore(lore);
		previewItemStack.setItemMeta(previewItemStackItemMeta);

		// Done :D
		valid = true;
		this.previewItemStack = previewItemStack;
	}

	public boolean isValid() {
		return valid;
	}

	public ItemStack getPreviewItemStack() {
		return previewItemStack.clone(); // Clone it so it can't be changed
	}

	public ItemStack getWinningItemStack() {
		return winningItemStack.clone(); // Clone it so it can't be changed and because Bukkit resets the stack size? Check issue #198 on this bug.
	}

	public void runWin(final Player player) {
		final Winning winning = this;
		Bukkit.getScheduler().runTask(HCFactions, new Runnable() {
			@Override
			public void run() {
				if (isCommand() && getCommands().size() > 0) {
					for (String command : getCommands()) {
						command = command.replaceAll("%name%", player.getName());
						command = command.replaceAll("%uuid%", player.getUniqueId().toString());
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
					}
				} else if (!isCommand()) {
					HashMap<Integer, ItemStack> left = player.getInventory().addItem(winning.getWinningItemStack());
					for (Map.Entry<Integer, ItemStack> item : left.entrySet()) {
						player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item.getValue());
					}
				}

				/** Do broadcast */
				if (crate.isBroadcast())
					Bukkit.broadcastMessage(HCFactions.getPluginPrefix() + HCFactions.getMessageHandler().getMessage("Broadcast", player, crate, winning));

				/** Spawn firework */
				if (crate.isFirework())
					HCFactions.getCrateHandler().spawnFirework(player.getLocation());
			}
		});
	}

	public boolean isCommand() {
		return command;
	}

	public double getPercentage() {
		return percentage;
	}

	public List<String> getCommands() {
		return commands;
	}
}
