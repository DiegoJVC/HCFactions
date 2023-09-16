package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.atheneum.command.Command;

public class CitadelCommand {

    @Command(names={ "citadel" }, permission="")
    public static void citadel(Player sender) {
        Set<ObjectId> cappers = HCFactions.getInstance().getCitadelHandler().getCappers();
        Set<String> capperNames = new HashSet<>();

        for (ObjectId capper : cappers) {
            Faction capperFaction = HCFactions.getInstance().getFactionHandler().getTeam(capper);

            if (capperFaction != null) {
                capperNames.add(capperFaction.getName());
            }
        }

        if (!capperNames.isEmpty()) {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + Joiner.on(", ").join(capperNames) + ChatColor.YELLOW + ".");
        } else {
            Event citadel = HCFactions.getInstance().getEventHandler().getEvent("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        }

        Date lootable = HCFactions.getInstance().getCitadelHandler().getLootable();
        sender.sendMessage(ChatColor.GOLD + "Citadel: " + ChatColor.WHITE + "Lootable " + (lootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(lootable) + (capperNames.isEmpty() ? "." : ", and lootable now by " + Joiner.on(", ").join(capperNames) + ".")));
    }

}