package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.HCFactionsConstants;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public final class TellLocationCommand {

    @Command(names = {"telllocation", "tl"}, permission = "")
    public static void tellLocation(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.DARK_RED + "You're not on a faction!");
            return;
        }

        if (faction.getHQ() == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Your faction doesn't have a home set!");
            return;
        }

        Location l = sender.getLocation();
        faction.sendMessage(HCFactionsConstants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", l.getX(), l.getY(), l.getZ())));
    }

}
