package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.commands.LastInvCommand;
import com.cobelpvp.hcfactions.server.EnderpearlCooldownHandler;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        LastInvCommand.recordInventory(event.getEntity());

        EnderpearlCooldownHandler.getEnderpearlCooldown().remove(event.getEntity().getName()); // cancel enderpearls

        if (HCFactions.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        int seconds = (int) HCFactions.getInstance().getServerHandler().getDeathban(event.getEntity());
        HCFactions.getInstance().getDeathbanMap().deathban(event.getEntity().getUniqueId(), seconds);

        final String time = TimeUtils.formatIntoDetailedString(seconds);

        new BukkitRunnable() {

            public void run() {
                if (!event.getEntity().isOnline()) {
                    return;
                }

                if (HCFactions.getInstance().getServerHandler().isPreEOTW()) {
                    event.getEntity().kickPlayer(ChatColor.RED + "Come back tomorrow for SOTW!");
                } else {
                    if (HCFactions.getInstance().getLivesMap().getLives(event.getEntity().getUniqueId()) <= 0) {
                        event.getEntity().kickPlayer(ChatColor.GREEN + "Come back in " + time + "!");
                    } else {
                        event.getEntity().kickPlayer(ChatColor.GREEN + "Come back in " + time + "!\n" + ChatColor.YELLOW + "You have " + HCFactions.getInstance().getLivesMap().getLives(event.getEntity().getUniqueId()) + " lives.");
                        HCFactions.getInstance().getDeathbanMap().revive(event.getEntity().getUniqueId());
                        return;
                    }
                }
            }

        }.runTaskLater(HCFactions.getInstance(), 10 * 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean shouldBypass = event.getPlayer().isOp();

        if (HCFactions.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
            if (HCFactions.getInstance().getLivesMap().getLives(event.getPlayer().getUniqueId()) >= 1) {
                HCFactions.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
                HCFactions.getInstance().getLivesMap().setLives(event.getPlayer().getUniqueId(), HCFactions.getInstance().getLivesMap().getLives(event.getPlayer().getUniqueId()) - 1);
                return;
            }
        }
        
        if (!shouldBypass) {
            shouldBypass = event.getPlayer().hasPermission("hcfactions.staff");
        }
        
        if (shouldBypass) {
            HCFactions.getInstance().getDeathbanMap().revive(event.getPlayer().getUniqueId());
        }
    }

}