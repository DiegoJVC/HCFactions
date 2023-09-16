package com.cobelpvp.hcfactions.events.systemfactions.citadel.listeners;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.event.HourEvent;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.events.CitadelActivatedEvent;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.events.CitadelCapturedEvent;
import com.cobelpvp.hcfactions.events.listeners.EventActivatedEvent;
import com.cobelpvp.hcfactions.events.listeners.EventCapturedEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;

public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivated(EventActivatedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            HCFactions.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(EventCapturedEvent event) {
        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

            if (playerFaction != null) {
                HCFactions.getInstance().getCitadelHandler().addCapper(playerFaction.getUniqueId());
                playerFaction.setCitadelsCapped(playerFaction.getCitadelsCapped() + 1);
            }
        }
    }

    @EventHandler
    public void onCitadelActivated(CitadelActivatedEvent event) {
        HCFactions.getInstance().getCitadelHandler().resetCappers();
    }

    @EventHandler
    public void onCitadelCaptured(CitadelCapturedEvent event) {
        HCFactions.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "Citadel" + ChatColor.YELLOW + " is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(HCFactions.getInstance().getCitadelHandler().getLootable()) + ChatColor.YELLOW + ".");
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

        if (playerFaction != null && HCFactions.getInstance().getCitadelHandler().getCappers().contains(playerFaction.getUniqueId())) {
            event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your faction currently controls Citadel.");
        }
    }


    @EventHandler
    public void onHour(HourEvent event) {
        if (event.getHour() % 2 == 0) {
            int respawned = HCFactions.getInstance().getCitadelHandler().respawnCitadelChests();

            if (respawned != 0) {
                HCFactions.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Citadel loot chests have respawned!");
            }
        }
    }

}