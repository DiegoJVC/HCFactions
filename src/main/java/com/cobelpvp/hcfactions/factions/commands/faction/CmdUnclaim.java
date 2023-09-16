package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Claim;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CmdUnclaim {

    @Command(names={ "f unclaim", "faction unclaim", "fac unclaim" }, permission="")
    public static void teamUnclaim(Player sender, @Param(name="all?", defaultValue="not_all?") String all) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (!(faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only faction co-leaders can do this.");
            return;
        }

        if (faction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
            return;
        }

        if (all.equalsIgnoreCase("all")) {
            int claims = faction.getClaims().size();
            int refund = 0;

            for (Claim claim : faction.getClaims()) {
                refund += Claim.getPrice(claim, faction, false);

                Location minLoc = claim.getMinimumPoint();
                Location maxLoc = claim.getMaximumPoint();

                TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                        "playerId", sender.getUniqueId(),
                        "playerName", sender.getName(),
                        "refund", Claim.getPrice(claim, faction, false),
                        "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                        "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
                ));
            }

            faction.setBalance(faction.getBalance() + refund);
            LandBoard.getInstance().clear(faction);
            faction.getClaims().clear();

            for (Subclaim subclaim : faction.getSubclaims()) {
                LandBoard.getInstance().updateSubclaim(subclaim);
            }

            faction.setSpawnersInClaim(0);
            faction.getSubclaims().clear();
            faction.setHQ(null);
            faction.flagForSave();

            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (faction.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed all of your faction's claims. (" + ChatColor.LIGHT_PURPLE + claims + " total" + ChatColor.YELLOW + ")");
                }
            }

            return;
        }

        if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && faction.ownsLocation(sender.getLocation())) {
            Claim claim = LandBoard.getInstance().getClaim(sender.getLocation());
            int refund = Claim.getPrice(claim, faction, false);

            faction.setBalance(faction.getBalance() + refund);
            faction.getClaims().remove(claim);

            for (Subclaim subclaim : new ArrayList<>(faction.getSubclaims())) {
                if (claim.contains(subclaim.getLoc1()) || claim.contains(subclaim.getLoc2())) {
                    faction.getSubclaims().remove(subclaim);
                    LandBoard.getInstance().updateSubclaim(subclaim);
                }
            }

            faction.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed " + ChatColor.LIGHT_PURPLE + claim.getFriendlyName() + ChatColor.YELLOW + ".");
            faction.flagForSave();

            LandBoard.getInstance().setTeamAt(claim, null);

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "refund", Claim.getPrice(claim, faction, false),
                    "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            if (faction.getHQ() != null && claim.contains(faction.getHQ())) {
                faction.setHQ(null);
                sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "You do not own this claim.");
        sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/faction unclaim all" + ChatColor.RED + ".");
    }

}