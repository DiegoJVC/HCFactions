package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdTeamPointBreakDown {

	@Command(names = { "faction pointbr", "faction pbr", "f pointbr", "f pbr" }, permission = "hcfactions.pointbr")
	public static void teamPointBreakDown(Player player, @Param(name="team", defaultValue="self") final Faction faction) {
		player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + faction.getName());

		for (String info : faction.getPointBreakDown()) {
			player.sendMessage(info);
		}
	}

}
