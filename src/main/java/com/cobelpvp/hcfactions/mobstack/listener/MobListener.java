package com.cobelpvp.hcfactions.mobstack.listener;

import com.cobelpvp.hcfactions.HCFactions;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class MobListener implements Listener {

    private final HCFactions plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!plugin.isStackable(entity)) {
            return;
        }
        Player killer = entity.getKiller();
        if (killer == null && event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent byEntityEvent = (EntityDamageByEntityEvent) event;
            if (byEntityEvent.getDamager() instanceof Player) {
                killer = (Player) byEntityEvent.getDamager();
            }
        }
        int amount = plugin.getStackAmount(entity);
        if (amount > 1 && entity.getHealth() - event.getFinalDamage() <= 0.0) {
            plugin.decrementStack(entity);
            double maxHealth = entity.getMaxHealth();
            if (maxHealth > 20.0) {
                entity.setMaxHealth(maxHealth = 20.0);
            }
            entity.setHealth(maxHealth);
            event.setDamage(0.1);
            LivingEntity rip = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
            rip.setHealth(0.1);
            rip.damage(999, killer);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityInternet(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity)) {
            return;
        }
        Player player = event.getPlayer();
        LivingEntity entity = (LivingEntity) event.getRightClicked();
        if (plugin.getStackAmount(entity) > 1) {
            ItemStack item = event.getPlayer().getItemInHand();
            if (item.getType() == Material.BUCKET) {
                return;
            }
            event.setCancelled(true);
            switch (entity.getType()) {
                case COW:
                    if (item.getType() == Material.WHEAT) {
                        if (plugin.canBreed((Ageable) entity)) {
                            if (item.getAmount() >= 2) {
                                plugin.breedStack(player, (Ageable) entity);
                            } else {
                                player.sendMessage("You need 2 wheat to breed a stacked Cow.");
                                player.updateInventory();
                            }
                        }
                    }
                    break;
                case SHEEP:
                    if (item.getType() == Material.WHEAT) {
                        if (plugin.canBreed((Ageable) entity)) {
                            if (item.getAmount() >= 2) {
                                plugin.breedStack(player, (Ageable) entity);
                            } else {
                                player.sendMessage("{0}You need 2 wheat to breed a stacked Sheep.");
                                player.updateInventory();
                            }
                        }
                    }
                    break;
                case PIG:
                    if (item.getType() == Material.CARROT_ITEM) {
                        if (plugin.canBreed((Ageable) entity)) {
                            if (item.getAmount() >= 2) {
                                plugin.breedStack(player, (Ageable) entity);
                            } else {
                                player.sendMessage("You need 2 carrots to breed a stacked Pig.");
                                player.updateInventory();
                            }
                        }
                    }
                    break;
                case CHICKEN:
                    if (item.getType() == Material.SEEDS) {
                        if (plugin.canBreed((Ageable) entity)) {
                            if (item.getAmount() >= 2) {
                                plugin.breedStack(player, (Ageable) entity);
                            } else {
                                player.sendMessage("You need 2 seeds to breed a stacked Chicken.");
                                player.updateInventory();
                            }
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            if (plugin.isStackable(event.getEntity())) {
                if (plugin.getNearbyCount(event.getEntity()) > plugin.getAreaLimit()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}