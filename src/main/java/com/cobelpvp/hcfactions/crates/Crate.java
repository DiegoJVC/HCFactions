package com.cobelpvp.hcfactions.crates;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.handlers.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Crate {

	private HCFactions HCFactions;
	private String name;
	private String slug;
	private ChatColor color = ChatColor.WHITE;
	private Material block = Material.CHEST;
	private boolean firework = false;
	private boolean broadcast = true;
	private boolean preview = true;
	private boolean hidePercentages = false;
	private double knockback = 0.0;
	private ArrayList<Winning> winnings = new ArrayList<>();
	private double totalPercentage = 0;
	private Key key;
	private HashMap<String, Location> locations = new HashMap<>();
	private String permission = null;

	private String opener = null;
	private Integer cooldown = null;

	public Crate(String name, HCFactions HCFactions, ConfigHandler configHandler) {
		this.HCFactions = HCFactions;
		this.name = name;
		this.slug = name.toLowerCase();

		if (HCFactions.getConfig().isSet("Crates." + name + ".Color"))
			this.color = ChatColor.valueOf(HCFactions.getConfig().getString("Crates." + name + ".Color").toUpperCase());
		if (HCFactions.getConfig().isSet("Crates." + name + ".Block"))
			this.block = Material.valueOf(HCFactions.getConfig().getString("Crates." + name + ".Block").toUpperCase());
		if (HCFactions.getConfig().isSet("Crates." + name + ".Firework"))
			this.firework = HCFactions.getConfig().getBoolean("Crates." + name + ".Firework");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Broadcast"))
			this.broadcast = HCFactions.getConfig().getBoolean("Crates." + name + ".Broadcast");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Preview"))
			this.preview = HCFactions.getConfig().getBoolean("Crates." + name + ".Preview");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Knockback"))
			this.knockback = HCFactions.getConfig().getDouble("Crates." + name + ".Knockback");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Permission"))
			this.permission = HCFactions.getConfig().getString("Crates." + name + ".Permission");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Hide Percentages"))
			this.hidePercentages = HCFactions.getConfig().getBoolean("Crates." + name + ".Hide Percentages");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Opener"))
			this.opener = HCFactions.getConfig().getString("Crates." + name + ".Opener");
		if (HCFactions.getConfig().isSet("Crates." + name + ".Cooldown"))
			this.cooldown = HCFactions.getConfig().getInt("Crates." + name + ".Cooldown");

		if (!HCFactions.getConfig().isSet("Crates." + name + ".Key") || !HCFactions.getConfig().isSet("Crates." + name + ".Key.Item") || !HCFactions.getConfig().isSet("Crates." + name + ".Key.Name") || !HCFactions.getConfig().isSet("Crates." + name + ".Key.Enchanted"))
			return;

		this.key = new Key(name, Material.valueOf(HCFactions.getConfig().getString("Crates." + name + ".Key.Item")), HCFactions.getConfig().getString("Crates." + name + ".Key.Name").replaceAll("%type%", getName(true)), HCFactions.getConfig().getBoolean("Crates." + name + ".Key.Enchanted"), HCFactions);

		if (!HCFactions.getConfig().isSet("Crates." + name + ".Winnings"))
			return;

		for (String id : HCFactions.getConfig().getConfigurationSection("Crates." + name + ".Winnings").getKeys(false)) {
			String path = "Crates." + name + ".Winnings." + id;
			Winning winning = new Winning(this, path, HCFactions, configHandler);
			if (totalPercentage + winning.getPercentage() > 100 || !winning.isValid()) {
				if (totalPercentage + winning.getPercentage() > 100)
					Bukkit.getLogger().warning("Your percentages must NOT add up to more than 100%");
				break;
			}
			totalPercentage = totalPercentage + winning.getPercentage();
			winnings.add(winning);
		}
	}

	public String getName() {
		return getName(false);
	}

	public String getName(boolean includecolor) {
		if (includecolor) return getColor() + this.name;
		return this.name;
	}

	public String getSlug() {
		return slug;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public Material getBlock() {
		return this.block;
	}

	public boolean isFirework() {
		return this.firework;
	}

	public boolean isBroadcast() {
		return this.broadcast;
	}

	public boolean isPreview() {
		return preview;
	}

	public boolean isHidePercentages() {
		return hidePercentages;
	}

	public double getKnockback() {
		return this.knockback;
	}

	public void reloadWinnings() {
		HCFactions.reloadConfig();
		winnings.clear();
		for (String id : HCFactions.getConfig().getConfigurationSection("Crates." + name + ".Winnings").getKeys(false)) {
			String path = "Crates." + name + ".Winnings." + id;
			Winning winning = new Winning(this, path, HCFactions, HCFactions.getConfigHandler());
			if (winning.isValid())
				winnings.add(winning);
		}
	}

	public List<Winning> getWinnings() {
		return winnings;
	}

	public void clearWinnings() {
		winnings.clear();
	}

	public void addWinning(Winning winning) {
		winnings.add(winning);
	}

	public double getTotalPercentage() {
		return totalPercentage;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setColor(String color) {
		this.color = ChatColor.valueOf(color);
		String path = "Crates." + name + ".Color";
		HCFactions.getConfig().set(path, color);
		HCFactions.saveConfig();
		HCFactions.reloadConfig();
	}

	public HashMap<String, Location> getLocations() {
		return locations;
	}

	public void addLocation(String string, Location location) {
		locations.put(string, location);
	}

	public Location getLocation(String key) {
		return locations.get(key);
	}

	public Location removeLocation(String key) {
		return locations.remove(key);
	}

	public String getPermission() {
		return permission;
	}

	public void addToConfig(Location location) {
		List<String> locations = new ArrayList<>();
		if (HCFactions.getDataConfig().isSet("Crate Locations." + this.getName(false).toLowerCase()))
			locations = HCFactions.getDataConfig().getStringList("Crate Locations." + this.getName(false).toLowerCase());
		locations.add(location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ());
		HCFactions.getDataConfig().set("Crate Locations." + this.getName(false).toLowerCase(), locations);
		try {
			HCFactions.getDataConfig().save(HCFactions.getDataFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeFromConfig(Location location) {
		List<String> locations = new ArrayList<>();
		if (HCFactions.getDataConfig().isSet("Crate Locations." + this.getName(false).toLowerCase()))
			locations = HCFactions.getDataConfig().getStringList("Crate Locations." + this.getName(false).toLowerCase());
		if (locations.contains(location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ()))
			locations.remove(location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ());
		HCFactions.getDataConfig().set("Crate Locations." + this.getName(false).toLowerCase(), locations);
		try {
			HCFactions.getDataConfig().save(HCFactions.getDataFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getOpener() {
		return opener;
	}

	public Integer getCooldown() {
		if (cooldown == null || cooldown < 0)
			return HCFactions.getConfigHandler().getDefaultCooldown();
		return cooldown;
	}

	public void setOpener(String opener) {
		this.opener = opener;
		HCFactions.getConfig().set("Crates." + getName(false) + ".Opener", opener);
		HCFactions.saveConfig();
	}

	public boolean containsCommandItem() {
		for (Winning winning : getWinnings()) {
			if (winning.isCommand())
				return true;
		}
		return false;
	}

}
