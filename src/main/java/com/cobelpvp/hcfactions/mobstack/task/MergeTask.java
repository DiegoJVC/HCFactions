package com.cobelpvp.hcfactions.mobstack.task;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.mobstack.util.Reflection;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MergeTask extends BukkitRunnable {

    private final HCFactions plugin;
    private final int PERIOD;
    private final Random random;
    private final List<Object> nameTagVisibleMetadata;
    private final Location loc;
    private int tick;

    public MergeTask(HCFactions plugin) {
        this.plugin = plugin;
        this.PERIOD = 20;
        this.random = new Random();

        Class<?> watchableObject = Reflection.getClass("{nms}.WatchableObject");
        Reflection.ConstructorInvoker watchableContructor = Reflection.getConstructor(watchableObject, int.class, int.class, Object.class);
        Object nameTagMeta = watchableContructor.invoke(0, 11, (byte) 1);
        this.nameTagVisibleMetadata = Collections.singletonList(nameTagMeta);

        this.loc = new Location(null, 0.0, 0.0, 0.0);
        this.tick = 0;
    }

    @Override
    public void run() {
        int chunkRadius = 3;
        for (World world : Bukkit.getWorlds()) {
            Set<Chunk> processed = new HashSet<>(500);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld() != world) {
                    continue;
                }
                player.getLocation(loc);
                int playerX = loc.getBlockX() >> 4;
                int playerZ = loc.getBlockZ() >> 4;
                for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                    for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                        if (Math.abs((playerX + dx) * 31 + playerZ + dz) % PERIOD == tick) {
                            Chunk chunk = player.getWorld().getChunkAt(playerX + dx, playerZ + dz);
                            if (processed.add(chunk) && plugin.getAreaChecker().canMergeAt(chunk)) {
                                mergeChunk(chunk);
                            }
                        }
                    }
                }
            }
        }
        tick = ++tick % PERIOD;
    }

    private void mergeChunk(Chunk chunk) {
        for (Entity _entity : chunk.getEntities()) {
            if (!(_entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity entity = (LivingEntity) _entity;
            if (entity.isDead() || entity.getHealth() <= 0.0 || !plugin.isStackable(entity)) {
                continue;
            }
            int amount = plugin.getStackAmount(entity);
            if (amount > 1 && plugin.getNearbyCount(entity) > plugin.getAreaLimit()) {
                plugin.decrementStack(entity);
            }
            for (Entity other : entity.getNearbyEntities(2, 2, 2)) {
                if (!(other instanceof LivingEntity)) {
                    continue;
                }
                LivingEntity otherLiving = (LivingEntity) other;
                if (otherLiving.isDead() || otherLiving.getHealth() <= 0.0 || !plugin.isStackable(otherLiving) || otherLiving.getType() != entity.getType()) {
                    continue;
                }
                int otherAmount = plugin.getStackAmount(otherLiving);
                if (amount >= otherAmount) {
                    otherLiving.remove();
                    plugin.addToStack(entity, otherAmount);
                }
            }
            int size = plugin.getStackAmount(entity);
            if (size > 1) {
                if (entity.getType() == EntityType.CHICKEN) {
                    if (random.nextInt(9000 / PERIOD) < size) {
                        entity.getWorld().dropItem(entity.getLocation().add(0, 0.5, 0), new ItemStack(Material.EGG));
                    }
                }
            }
        }
    }
}
