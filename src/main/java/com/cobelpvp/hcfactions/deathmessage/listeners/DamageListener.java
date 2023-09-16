package com.cobelpvp.hcfactions.deathmessage.listeners;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.deathmessage.DeathMessageHandler;
import com.cobelpvp.hcfactions.deathmessage.event.CustomPlayerDamageEvent;
import com.cobelpvp.hcfactions.deathmessage.util.UnknownDamage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import com.cobelpvp.hcfactions.deathmessage.objects.Damage;
import com.cobelpvp.hcfactions.deathmessage.objects.PlayerDamage;

public class DamageListener implements Listener {

    private Map<UUID, UUID> lastKilled = Maps.newHashMap();
    private Map<UUID, Integer> boosting = Maps.newHashMap();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));

            HCFactions.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        DeathMessageHandler.clearDamage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());

        event.setDeathMessage(null);

        String deathMessage;

        if (record != null) {
            Damage deathCause = record.get(record.size() - 1);

            if (deathCause instanceof PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = HCFactions.getInstance().getServer().getPlayerExact(killerName);

                if (killer != null && !HCFactions.getInstance().getInDuelPredicate().test(event.getEntity())) {
                    ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();
                }
            }

            deathMessage = deathCause.getDeathMessage();
        } else {
            deathMessage = new UnknownDamage(event.getEntity().getName(), 1).getDeathMessage();
        }

        Player killer = event.getEntity().getKiller();

        Faction killerFaction = killer == null ? null : HCFactions.getInstance().getFactionHandler().getTeam(killer);
        Faction deadFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getEntity());

        if (killerFaction != null) {
            killerFaction.setKills(killerFaction.getKills() + 1);
        }

        if (deadFaction != null) {
            deadFaction.setDeaths(deadFaction.getDeaths() + 1);
        }

        Bukkit.getScheduler().runTaskAsynchronously(HCFactions.getInstance(), () -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage(ColorText.translate(deathMessage));
            }
        });

        DeathMessageHandler.clearDamage(event.getEntity());
        HCFactions.getInstance().getDeathsMap().setDeaths(event.getEntity().getUniqueId(), HCFactions.getInstance().getDeathsMap().getDeaths(event.getEntity().getUniqueId()) + 1);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    }

    @EventHandler(ignoreCancelled = false)
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().name().startsWith("RIGHT_CLICK")) {
            return;
        }

        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand == null) {
            return;
        }

        if (inHand.getType() != Material.NETHER_STAR) {
            return;
        }

        if (!inHand.hasItemMeta() || !inHand.getItemMeta().hasDisplayName() || !inHand.getItemMeta().getDisplayName().startsWith(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Potion Refill Token")) {
            return;
        }

        event.getPlayer().setItemInHand(null);

        ItemStack pot = new ItemStack(Material.POTION, 1, (short) 16421);
        while (event.getPlayer().getInventory().addItem(pot).isEmpty()) {
        }
    }

}
