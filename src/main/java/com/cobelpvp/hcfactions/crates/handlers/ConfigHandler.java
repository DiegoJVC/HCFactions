package com.cobelpvp.hcfactions.crates.handlers;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class ConfigHandler {

	private Integer defaultCooldown = 0;
	private Integer crateGUITime = 10;
	private String defaultOpener = "NoGUI";
	private HashMap<String, Crate> crates = new HashMap<>();
	private boolean disableKeySwapping = false;
	private boolean debugMode = false;

	public ConfigHandler(FileConfiguration config, HCFactions HCFactions) {
		// Load configuration
		if (config.isSet("Cooldown")) {
			config.set("Default Cooldown", config.getInt("Cooldown"));
			config.set("Cooldown", null);
			HCFactions.saveConfig();
		}

		if (config.isSet("Debug Mode")) {
			debugMode = config.getBoolean("Debug Mode", false);
		}

		if (config.isSet("Disable Key Dropping")) {
			config.set("Disable Key Swapping", config.getBoolean("Disable Key Dropping"));
			config.set("Disable Key Dropping", null);
			HCFactions.saveConfig();
		}

		if (config.isSet("Disable Key Swapping"))
			disableKeySwapping = config.getBoolean("Disable Key Swapping");

		if (config.isSet("Default Cooldown"))
			setDefaultCooldown(config.getInt("Default Cooldown"));

		// Register Crates
		if (config.isSet("Crates")) {
			for (String crate : config.getConfigurationSection("Crates").getKeys(false)) {
				addCrate(crate.toLowerCase(), new Crate(crate, HCFactions, this));
			}
		}

		// Crate GUI
		if (config.isSet("Use GUI")) {
			if (config.getBoolean("Use GUI")) {
				config.set("Default Opener", "BasicGUI");
			} else {
				config.set("Default Opener", "NoGUI");
			}
			config.set("Use GUI", null);
			HCFactions.saveConfig();
		}

		// Default Opener
		if (config.isSet("Default Opener"))
			defaultOpener = config.getString("Default Opener");

		// Crate GUI Time, this is now moved into the BasicGUI opener
		if (config.isSet("GUI Time")) {
			crateGUITime = config.getInt("GUI Time");
			config.set("GUI Time", null);
			HCFactions.saveConfig();
		}
	}

	public Integer getDefaultCooldown() {
		return defaultCooldown;
	}

	public void setDefaultCooldown(int defaultCooldown) {
		this.defaultCooldown = defaultCooldown;
	}

	public void setCrates(HashMap<String, Crate> crates) {
		this.crates = crates;
	}

	public void addCrate(String name, Crate crate) {
		this.crates.put(name, crate);
	}

	public Crate getCrate(String name) {
		if (this.crates.containsKey(name))
			return this.crates.get(name);
		return null;
	}

	public HashMap<String, Crate> getCrates() {
		return this.crates;
	}

	@Deprecated
	public Integer getCrateGUITime() {
		return crateGUITime;
	}

	public String getDefaultOpener() {
		return defaultOpener;
	}

	public boolean isDisableKeySwapping() {
		return disableKeySwapping;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

}
