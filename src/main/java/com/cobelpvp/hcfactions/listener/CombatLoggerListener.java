package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.commands.CustomTimerCreateCommand;
import com.cobelpvp.hcfactions.commands.LastInvCommand;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import com.cobelpvp.atheneum.serialization.PlayerInventorySerializer;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
    @Getter private Set<Entity> combatLoggers = new HashSet<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            combatLoggers.remove(event.getEntity());
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (!metadata.playerName.equals(event.getEntity().getCustomName().substring(2))) {
                HCFactions.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + metadata.playerName + " (" + event.getEntity().getCustomName().substring(2) + ")");
            }

            HCFactions.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

            HCFactions.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);

            int deaths = HCFactions.getInstance().getDeathsMap().getDeaths(metadata.playerUUID);
            HCFactions.getInstance().getDeathsMap().setDeaths(metadata.playerUUID, deaths + 1);

            Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID);

            if (faction != null) {
                faction.playerDeath(metadata.playerName, HCFactions.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()));
            }

            for (ItemStack item : metadata.contents) {
                event.getDrops().add(item);
            }

            for (ItemStack item : metadata.armor) {
                event.getDrops().add(item);
            }

            int victimKills = HCFactions.getInstance().getKillsMap().getKills(event.getEntity().getUniqueId());

            if (event.getEntity().getKiller() != null) {
                HCFactions.getInstance().getKillsMap().setKills(event.getEntity().getKiller().getUniqueId(), HCFactions.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getUniqueId()) + 1);

                int killerKills = HCFactions.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getUniqueId());

                List<String> names = Lists.newArrayList();

                names.add(" kill ");
                names.add(" kill ");
                Random random = new Random();
                String randomMessage = names.get(random.nextInt(names.size()));

                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + randomMessage + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.YELLOW + ".";

                for (Player player : Bukkit.getOnlinePlayers()) {
                        if (HCFactions.getInstance().getFactionHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID) != null
                                && HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID).equals(HCFactions.getInstance().getFactionHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }

                        if (HCFactions.getInstance().getFactionHandler().getTeam(event.getEntity().getKiller().getUniqueId()) != null
                                && HCFactions.getInstance().getFactionHandler().getTeam(event.getEntity().getKiller().getUniqueId()).equals(HCFactions.getInstance().getFactionHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                }
            } else {
                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

                for (Player player : Bukkit.getOnlinePlayers()) {
                        if (HCFactions.getInstance().getFactionHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID) != null
                                && HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID).equals(HCFactions.getInstance().getFactionHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                }
            }

            Player target = HCFactions.getInstance().getServer().getPlayer(metadata.playerUUID);

            if (target == null) {
                MinecraftServer server = ((CraftServer) HCFactions.getInstance().getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(metadata.playerUUID, metadata.playerName), new PlayerInteractManager(server.getWorldServer(0)));
                target = entity.getBukkitEntity();

                if (target != null) {
                    target.loadData();
                }
            }

            if (target != null) {
                EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                target.getInventory().clear();
                target.getInventory().setArmorContents(null);
                humanTarget.setHealth(0);

                spoofWebsiteData(target, event.getEntity().getKiller());
                target.saveData();
            }

            LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Iterator<Entity> combatLoggerIterator = combatLoggers.iterator();

        while (combatLoggerIterator.hasNext()) {
            Pig villager = (Pig) combatLoggerIterator.next();

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {
                villager.remove();
                combatLoggerIterator.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null) {
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (DTRHCFClaim.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRHCFClaim.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (HCFactions.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID);

            if (faction != null && faction.isMember(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
        }
    }

    @EventHandler
    public void onEntityPressurePlate(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.STONE_PLATE && event.getEntity() instanceof Pig && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().hasMetadata("loggedout")) {
            event.getPlayer().removeMetadata("loggedout", HCFactions.getInstance());
            return;
        }

        if (event.getPlayer().hasMetadata("invisible") || event.getPlayer().hasMetadata("modmode")) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            return;
        }

        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            return;
        }

        if (event.getPlayer().isDead()) {
            return;
        }

        if (event.getPlayer().hasMetadata("frozen")) {
            return;
        }

        if (event.getPlayer().getLocation().getBlockY() <= 0) {
            return;
        }

        boolean spawnCombatLogger = false;

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 40, 40)) {
            if (entity instanceof Player) {
                Player other = (Player) entity;

                if (other.hasMetadata("invisible")) {
                    continue;
                }

                Faction otherFaction = HCFactions.getInstance().getFactionHandler().getTeam(other);
                Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

                if (otherFaction != playerFaction || playerFaction == null) {
                    spawnCombatLogger = true;
                    break;
                }
            }
        }

        if (!event.getPlayer().isOnGround()) {
            spawnCombatLogger = true;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !event.getPlayer().hasMetadata("invisible") && spawnCombatLogger && !event.getPlayer().isDead()) {
            HCFactions.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            ItemStack[] inv = event.getPlayer().getInventory().getContents();

            Pig villager = (Pig) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.PIG);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

            if (event.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                    if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
                        villager.addPotionEffect(potionEffect);
                        break;
                    }
                }
            }

            CombatLoggerMetadata metadata = new CombatLoggerMetadata();

            metadata.playerName = event.getPlayer().getName();
            metadata.playerUUID = event.getPlayer().getUniqueId();
            metadata.deathBanTime = HCFactions.getInstance().getServerHandler().getDeathban(metadata.playerUUID, event.getPlayer().getLocation());
            metadata.contents = inv;
            metadata.armor = armor;

            villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(HCFactions.getInstance(), metadata));

            villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
            villager.setHealth(villager.getMaxHealth());

            villager.setCustomName(ChatColor.YELLOW.toString() + event.getPlayer().getName());
            villager.setCustomNameVisible(true);

            villager.setFallDistance(event.getPlayer().getFallDistance());
            villager.setRemoveWhenFarAway(false);
            villager.setVelocity(event.getPlayer().getVelocity());

            combatLoggers.add(villager);

            new BukkitRunnable() {

                public void run() {
                    if (!villager.isDead() && villager.isValid()) {
                        combatLoggers.remove(villager);
                        villager.remove();
                    }
                }

            }.runTaskLater(HCFactions.getInstance(), 30 * 20L);

            if (villager.getWorld().getEnvironment() == World.Environment.THE_END) {
                new BukkitRunnable() {

                    int tries = 0;

                    @Override
                    public void run() {
                        if (villager.getLocation().getBlockY() >= 0) {
                            tries++;

                            if (tries == 30) {
                                cancel();
                            }
                            return;
                        }

                        HCFactions.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + villager.getLocation().getBlockX() + ", " + villager.getLocation().getBlockY() + ", " + villager.getLocation().getBlockZ() + ") died.");

                        HCFactions.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);
                        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(metadata.playerUUID);

                        if (faction != null) {
                            faction.playerDeath(metadata.playerName, HCFactions.getInstance().getServerHandler().getDTRLoss(villager.getLocation()));
                        }

                        int victimKills = HCFactions.getInstance().getKillsMap().getKills(metadata.playerUUID);

                        String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]" +  ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                                if (faction != null && faction == HCFactions.getInstance().getFactionHandler().getTeam(player.getUniqueId())) {
                                    player.sendMessage(deathMessage);
                                }
                        }

                        Player target = HCFactions.getInstance().getServer().getPlayer(metadata.playerUUID);

                        if (target == null) {
                            MinecraftServer server = ((CraftServer) HCFactions.getInstance().getServer()).getServer();
                            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(metadata.playerUUID, metadata.playerName), new PlayerInteractManager(server.getWorldServer(0)));
                            target = entity.getBukkitEntity();

                            if (target != null) {
                                target.loadData();
                            }
                        }

                        if (target != null) {
                            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                            target.getInventory().clear();
                            target.getInventory().setArmorContents(null);
                            humanTarget.setHealth(0);

                            spoofWebsiteData(target, villager.getKiller());
                            target.saveData();
                        }

                        LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

                        cancel();
                        villager.remove();
                    }

                }.runTaskTimer(HCFactions.getInstance(), 0L, 20L);
            }
        }
    }

    public double calculateCombatLoggerHealth(Player player) {
        int potions = 0;
        boolean gapple = false;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16421) {
                potions++;
            } else if (!gapple && itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == (short) 1) {
                potions += 15;
                gapple = true;
            }
        }

        return ((potions * 3.5D) + player.getHealth());
    }

    public static class CombatLoggerMetadata {

        private ItemStack[] contents;
        private ItemStack[] armor;
        private String playerName;
        private UUID playerUUID;
        private long deathBanTime;

    }

    private void spoofWebsiteData(Player killed, Player killer) {
        final BasicDBObject playerDeath = new BasicDBObject();

        if (killer != null) {
            playerDeath.append("healthLeft", (int) killer.getHealth());
            playerDeath.append("killerUUID", killer.getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerInventory", PlayerInventorySerializer.getInsertableObject(killer));
        } else {
            try{
                playerDeath.append("reason", "combat-logger");
            } catch (NullPointerException ignored) {}
        }

        playerDeath.append("playerInventory", PlayerInventorySerializer.getInsertableObject(killed));
        playerDeath.append("uuid", killed.getUniqueId().toString().replace("-", ""));
        playerDeath.append("player", killed.getName());
        playerDeath.append("when", new Date());
        playerDeath.put("_id", UUID.randomUUID().toString().replaceAll("-", ""));

        new BukkitRunnable() {
            @Override
            public void run() {
                HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

}