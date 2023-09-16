package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NetherPortalListener implements Listener {

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event){
        Player player = event.getPlayer();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getFrom())){
                event.setCancelled(true);

                player.teleport(HCFactions.getInstance().getServer().getWorld("world").getSpawnLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to overworld spawn!");
            }
        }

        Location to = event.getTo();

        if (DTRHCFClaim.ROAD.appliesAt(to)) {
            Faction faction = LandBoard.getInstance().getTeam(to);

            if (faction.getName().contains("North")) {
                to.add(20, 0, 0);
            } else if (faction.getName().contains("South")) {
                to.subtract(20, 0, 0);
            } else if (faction.getName().contains("East")) {
                to.add(0, 0, 20);
            } else if (faction.getName().contains("West")) {
                to.subtract(0, 0, 20);
            }
        }

        event.setTo(to);
    }

}