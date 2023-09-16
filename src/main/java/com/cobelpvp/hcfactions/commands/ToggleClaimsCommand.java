package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class ToggleClaimsCommand {

    @Command(names={ "ToggleClaims" }, permission="hcfactions.toggleclaims")
    public static void toggleClaims(Player sender) {
        LandBoard.getInstance().setClaimsEnabled(!LandBoard.getInstance().isClaimsEnabled());
        sender.sendMessage(ChatColor.YELLOW + "Claims enabled? " + ChatColor.LIGHT_PURPLE + (LandBoard.getInstance().isClaimsEnabled() ? "Yes" : "No"));
    }

}