package com.cobelpvp.hcfactions.events.systemfactions.destroythecore;

import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.EventType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.cobelpvp.hcfactions.events.Event;

public class DTCListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Location location = event.getBlock().getLocation();
        if (!DTRHCFClaim.DTC.appliesAt(location)) {
            return;
        }
        
        DTC activeDTC = (DTC) HCFactions.getInstance().getEventHandler().getEvents().stream().filter(Event::isActive).filter(ev -> ev.getType() == EventType.DTC).findFirst().orElse(null);
        
        if (activeDTC == null) {
            return;
        }

        if (event.getBlock().getType() == Material.OBSIDIAN) {
            event.setCancelled(true);
            activeDTC.blockBroken(event.getPlayer());
        }
    }
    
}
