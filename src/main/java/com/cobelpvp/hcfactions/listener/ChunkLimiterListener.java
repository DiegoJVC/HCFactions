package com.cobelpvp.hcfactions.listener;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.HashMap;
import java.util.UUID;

public class ChunkLimiterListener implements Listener {

    private HashMap<UUID, Integer> defaultView = new HashMap<>();
    @Getter private static HashMap<UUID, Integer> viewDistances = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        defaultView.remove(event.getPlayer().getUniqueId());
        viewDistances.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockY() >> 4 == event.getTo().getBlockY() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4) {
            return;
        }

        if( viewDistances.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
    }

}
