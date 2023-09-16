package com.cobelpvp.hcfactions.factions.commands.faction.subclaim;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimUnclaimCommand {

    @Command(names = {"f subclaim unclaim", "faction subclaim unclaim", "fac subclaim unclaim", "f subclaim unsubclaim", "faction subclaim unsubclaim", "fac subclaim unsubclaim", "f unsubclaim", "faction unsubclaim", "fac unsubclaim"}, permission = "")
    public static void teamSubclaimUnclaim(Player sender, @Param(name = "subclaim", defaultValue = "location") Subclaim subclaim) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId())) {
            faction.getSubclaims().remove(subclaim);
            LandBoard.getInstance().updateSubclaim(subclaim);
            faction.flagForSave();
            sender.sendMessage(ChatColor.RED + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.RED + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Only faction captains can unclaim subclaims!");
        }
    }

}