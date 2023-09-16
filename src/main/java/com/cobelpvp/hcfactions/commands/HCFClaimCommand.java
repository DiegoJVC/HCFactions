package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class HCFClaimCommand {

    //TODO: Cleanup

    @Command(names={ "hcfclaim list", "hcfclaims list" }, permission="hcfactions.hcfclaim")
    public static void hcfclaimList(Player sender) {
        for (DTRHCFClaim hcfclaimType : DTRHCFClaim.values()) {
            sender.sendMessage(ChatColor.GOLD + hcfclaimType.getName() + " (" + hcfclaimType.getHcfclaim() + "): " + ChatColor.YELLOW + hcfclaimType.getDescription());
        }
    }

    @Command(names={ "hcfclaim info", "hcfclaims info" }, permission="hcfactions.hcfclaim")
    public static void hcfclaimInfo(Player sender, @Param(name="team") Faction faction) {
        if (faction.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "HCFClaim flags cannot be applied to teams without a null leader.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "HCFClaim flags of " + ChatColor.GOLD + faction.getName() + ChatColor.YELLOW + ":");

        for (DTRHCFClaim hcfclaimType : DTRHCFClaim.values()) {
            if (!faction.hasDTRHCFClaim(hcfclaimType)) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + hcfclaimType.getName() + " (" + hcfclaimType.getHcfclaim() + "): " + ChatColor.YELLOW + hcfclaimType.getDescription());
        }

        sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + faction.getDTR());
    }

    @Command(names={ "hcfclaim add", "hcfclaims add" }, permission="hcfactions.hcfclaim")
    public static void hcfclaimAdd(Player sender, @Param(name="target") Faction faction, @Param(name="hcfclaim") DTRHCFClaim hcfclaim) {
        if (faction.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "HCFClaim flags cannot be applied to teams without a null leader.");
            return;
        }

        if (faction.hasDTRHCFClaim(hcfclaim)) {
            sender.sendMessage(ChatColor.RED + "This claim already has the hcfclaim value " + hcfclaim.getName() + ".");
            return;
        }

        int dtrInt = (int) faction.getDTR();

        dtrInt += hcfclaim.getHcfclaim();

        faction.setDTR(dtrInt);
        hcfclaimInfo(sender, faction);
    }

    @Command(names={ "hcfclaim remove", "hcfclaims remove" }, permission="hcfactions.hcfclaim")
    public static void hcfclaimRemove(Player sender, @Param(name="team") Faction faction, @Param(name="hcfclaim") DTRHCFClaim hcfclaim) {
        if (faction.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "HCFClaim flags cannot be applied to teams without a null leader.");
            return;
        }

        if (!faction.hasDTRHCFClaim(hcfclaim)) {
            sender.sendMessage(ChatColor.RED + "This claim doesn't have the hcfclaim value " + hcfclaim.getName() + ".");
            return;
        }

        int dtrInt = (int) faction.getDTR();

        dtrInt -= hcfclaim.getHcfclaim();

        faction.setDTR(dtrInt);
        hcfclaimInfo(sender, faction);
    }

}