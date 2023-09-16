package com.cobelpvp.hcfactions.factions.commands.faction.subclaim;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimListCommand {

    @Command(names = {"f subclaim list", "faction subclaim list", "fac subclaim list", "f sub list", "faction sub list", "fac sub list"}, permission = "")
    public static void teamSubclaimList(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a faction to execute this command!");
            return;
        }

        StringBuilder access = new StringBuilder();
        StringBuilder other = new StringBuilder();

        for (Subclaim subclaim : faction.getSubclaims()) {
            if (subclaim.isMember(sender.getUniqueId()) || faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId())) {
                access.append(subclaim.getName()).append(", ");
                continue;
            }

            other.append(subclaim).append(", ");
        }

        if (access.length() > 2) {
            access.setLength(access.length() - 2);
        }

        if (other.length() > 2) {
            other.setLength(other.length() - 2);
        }

        sender.sendMessage(ChatColor.BLUE + faction.getName() + ChatColor.YELLOW + " Subclaim List");
        sender.sendMessage(ChatColor.YELLOW + "Subclaims you can access: " + ChatColor.WHITE + access.toString());
        sender.sendMessage(ChatColor.YELLOW + "Other Subclaims: " + ChatColor.WHITE + other.toString());
    }

}