package com.cobelpvp.hcfactions.crates.events;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CrateOpenEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private HCFactions HCFactions;
	private Player player;
	private Crate crate;
	private Location blockLocation;

	public CrateOpenEvent(Player player, String crateName, Location blockLocation, HCFactions HCFactions) {
		this.HCFactions = HCFactions;
		this.player = player;
		this.blockLocation = blockLocation;
		this.crate = HCFactions.getConfigHandler().getCrates().get(crateName.toLowerCase());
	}

	public void doEvent() {
		com.cobelpvp.hcfactions.HCFactions.getInstance().getOpenHandler().getOpener(crate).startOpening(player, crate, blockLocation);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Crate getCrate() {
		return this.crate;
	}

	public Location getBlockLocation() {
		return blockLocation;
	}

	public HCFactions getHCFactions() {
		return HCFactions;
	}

}