package com.cobelpvp.hcfactions.crates.handlers;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import com.cobelpvp.hcfactions.crates.opener.BasicGUIOpener;
import com.cobelpvp.hcfactions.crates.opener.NoGUIOpener;
import com.cobelpvp.hcfactions.crates.opener.Opener;

import java.util.HashMap;

public class OpenHandler {

	private HCFactions HCFactions;
	private HashMap<String, Opener> registered = new HashMap<>();
	private String defaultOpener;

	public OpenHandler(HCFactions HCFactions) {
		this.HCFactions = HCFactions;
		registerDefaults();
	}

	private void registerDefaults() {
		registerOpener(new BasicGUIOpener(HCFactions));
		registerOpener(new NoGUIOpener(HCFactions));
		defaultOpener = HCFactions.getConfigHandler().getDefaultOpener();
	}

	public void registerOpener(Opener opener) {
		if (registered.containsKey(opener.getName())) {
			HCFactions.getLogger().warning("An opener with the name \"" + opener.getName() + "\" already exists and will not be registered");
			return;
		}
		try {
			opener.doSetup();
			registered.put(opener.getName(), opener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Opener getOpener(Crate crate) {
		if (registered.containsKey(crate.getOpener()))
			return registered.get(crate.getOpener());
		return getDefaultOpener();
	}

	public Opener getDefaultOpener() {
		if (registered.containsKey(defaultOpener))
			return registered.get(defaultOpener);
		return registered.get("NoGUI");
	}

	public void setDefaultOpener(String defaultOpener) {
		this.defaultOpener = defaultOpener;
		HCFactions.getConfig().set("Default Opener", defaultOpener);
		HCFactions.saveConfig();
	}

	public boolean openerExist(String name) {
		return registered.containsKey(name);
	}

	public HashMap<String, Opener> getRegistered() {
		return registered;
	}

}
