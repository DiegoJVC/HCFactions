package com.cobelpvp.hcfactions.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AntiKeyRenameListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        if (command.startsWith("/rename")) {
            if (player.getItemInHand().getType() == Material.TRIPWIRE_HOOK || player.getItemInHand().getType() == Material.ENDER_CHEST || player.getItemInHand().getType() == Material.BEACON) {
                player.sendMessage(ChatColor.DARK_RED + ("You cant do this."));
                event.setCancelled(true);
            }
            return;
        }
    }
}
