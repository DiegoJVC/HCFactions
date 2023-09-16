package com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.listeners;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.GlowHandler;
import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.GlowMountain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GlowListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        GlowHandler glowHandler = HCFactions.getInstance().getGlowHandler();
        GlowMountain glowMountain = glowHandler.getGlowMountain();
        Faction factionAt = LandBoard.getInstance().getTeam(location);

        if (HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || !glowHandler.hasGlowMountain() || event.getBlock().getType() != Material.GLOWSTONE) {
            return;
        }

        if (factionAt == null || !factionAt.getName().equals(GlowHandler.getGlowTeamName())) {
            return;
        }

        if(!glowMountain.getGlowstone().contains(location.toVector().toBlockVector())) {
            return;
        }

        event.setCancelled(false);

        glowMountain.setRemaining(glowMountain.getRemaining() - 1);

        double total = glowMountain.getGlowstone().size();
        double remaining = glowMountain.getRemaining();


        if (total == remaining) {
            Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + GREEN + " 50% of Glowstone has been mined!");
        } else if (remaining == 0) {
            Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + RED + "  All Glowstone has been mined!");
        }
    }
}