package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelRespawnChestsCommand {

    @Command(names={"citadel respawnchests"}, permission="hcfactions.citadel.admin")
    public static void citadelRespawnChests(Player sender) {
        HCFactions.getInstance().getCitadelHandler().respawnCitadelChests();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Respawned all Citadel chests.");
    }

}