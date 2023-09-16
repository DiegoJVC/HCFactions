package com.cobelpvp.hcfactions.factions.commands.faction.subclaim;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamSubclaimAddPlayerCommand {

    @Command(names = {"f subclaim addplayer", "faction subclaim addplayer", "fac subclaim addplayer", "f sub addplayer", "faction sub addplayer", "fac sub addplayer", "f subclaim grant", "faction subclaim grant", "fac subclaim grant", "f sub grant", "faction sub grant", "fac sub grant"}, permission = "")
    public static void teamSubclaimAddPlayer(Player sender, @Param(name = "subclaim") Subclaim subclaim, @Param(name = "player") UUID player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (!faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId()) && !faction.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the faction captains can do this.");
            return;
        }

        if (!faction.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on your faction!");
            return;
        }

        if (subclaim.isMember(player)) {
            sender.sendMessage(ChatColor.RED + "The player already has access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " has been added to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.addMember(player);
        faction.flagForSave();
    }

}