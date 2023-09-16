package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.eotw.EOTW;
import lombok.Getter;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class GoldenAppleListener implements Listener {

    @Getter private static Map<UUID, Long> crappleCooldown = new HashMap<>();

    @EventHandler(ignoreCancelled=false)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();

        if (event.getItem() == null || event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        long cooldown = 7000;

        if (!HCFactions.getInstance().getServerHandler().isUhcHealing()) {
            if (event.getItem().getDurability() == 0 && !crappleCooldown.containsKey(player.getUniqueId())) {
                crappleCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (cooldown));
                return;
            }

            if (event.getItem().getDurability() == 0 && crappleCooldown.containsKey(player.getUniqueId())) {
                long millisRemaining = crappleCooldown.get(player.getUniqueId()) - System.currentTimeMillis();
                double value = (millisRemaining / 1000D);
                double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;

                if (crappleCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
                    event.setCancelled(true);
                    return;
                } else {
                    crappleCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
                    return;
                }
            }
        }

        if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 0) return;

        if (HCFactions.getInstance().getMapHandler().getGoppleCooldown() == -1) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Super golden apples are currently disabled.");
            return;
        }

        long cooldownUntil = HCFactions.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId());

        if (cooldownUntil > System.currentTimeMillis()) {
            long millisLeft = cooldownUntil - System.currentTimeMillis();
            String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You are still on cooldown for another §c§l" + msg + "§c.");
            return;
        }

        HCFactions.getInstance().getOppleMap().useGoldenApple(
                event.getPlayer().getUniqueId(),
                (HCFactions.getInstance().getMapHandler().getGoppleCooldown() * 60) // minutes to seconds
        );
        long millisLeft = HCFactions.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId()) - System.currentTimeMillis();

        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.YELLOW + "██" + ChatColor.DARK_GREEN + "███");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.YELLOW + "█" + ChatColor.DARK_GREEN + "████");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + " Golden Apple:");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██" + ChatColor.WHITE + "█" + ChatColor.GOLD + "███" + ChatColor.DARK_GREEN + "█" + ChatColor.DARK_GREEN + "   Consumed");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "█" + ChatColor.WHITE + "█" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "█" + ChatColor.YELLOW + " Cooldown Remaining:");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█" + ChatColor.BLUE + "   " + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██");
    }

}
