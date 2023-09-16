package com.cobelpvp.hcfactions.factions.commands.faction.subclaim;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.VisualClaim;
import com.cobelpvp.hcfactions.factions.claims.VisualClaimType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimStartCommand {

    @Command(names = {"f subclaim start", "faction subclaim start", "fac subclaim start", "f sub start", "faction sub start", "fac sub start"}, permission = "")
    public static void teamSubclaimStart(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a faction to execute this command!");
            return;
        }

        if (!faction.isCaptain(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId()) && !faction.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
            return;
        }

        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (sender.getInventory().getItem(i) == null) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the subclaim wand!");
            return;
        }

        if (!VisualClaim.getCurrentSubclaimMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, true).draw(true);
        }

        sender.getInventory().setItem(slot, TeamSubclaimCommand.SELECTION_WAND.clone());
        sender.sendMessage(ChatColor.GREEN + "Gave you a subclaim wand.");
    }

}