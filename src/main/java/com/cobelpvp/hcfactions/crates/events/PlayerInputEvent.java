package com.cobelpvp.hcfactions.crates.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInputEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	public String[] lines;

	public PlayerInputEvent(Player player, String[] lines) {
		this.player = player;
		this.lines = lines;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public String[] getLines() {
		return lines;
	}

}