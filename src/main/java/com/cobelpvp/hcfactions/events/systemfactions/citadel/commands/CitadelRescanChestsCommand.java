package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelRescanChestsCommand {

    @Command(names={"citadel rescanchests"}, permission="hcfactions.citadel.admin")
    public static void citadelRescanChests(Player sender) {
        HCFactions.getInstance().getCitadelHandler().scanLoot();
        HCFactions.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.GOLD + "Rescanned all Citadel chests.");
    }

}