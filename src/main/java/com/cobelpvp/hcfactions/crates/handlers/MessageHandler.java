package com.cobelpvp.hcfactions.crates.handlers;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import com.cobelpvp.hcfactions.crates.Winning;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageHandler {

	private HCFactions HCFactions;

	public MessageHandler(HCFactions HCFactions) {
		this.HCFactions = HCFactions;
	}

	public String getMessage(String messageName, Player player, Crate crate, Winning winning) {
		if (!HCFactions.getMessagesConfig().isSet(messageName))
			return "Message \"" + messageName + "\" not configured";
		String message = HCFactions.getMessagesConfig().getString(messageName);
		message = doPlaceholders(message, player, crate, winning);
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}

	public String doPlaceholders(String message, Player player, Crate crate, Winning winning) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		if (player != null)
			message = message.replaceAll("%name%", player.getName()).replaceAll("%displayname%", player.getDisplayName()).replaceAll("%uuid%", player.getUniqueId().toString());
		if (crate != null)
			message = message.replaceAll("%crate%", crate.getName(true) + ChatColor.RESET);
		if (winning != null)
			message = message.replaceAll("%prize%", winning.getWinningItemStack().getItemMeta().getDisplayName() + ChatColor.RESET).replaceAll("%winning%", winning.getWinningItemStack().getItemMeta().getDisplayName() + ChatColor.RESET).replaceAll("%percentage%", String.valueOf(winning.getPercentage()));
		return message;
	}

}
