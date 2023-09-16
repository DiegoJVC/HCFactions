package com.cobelpvp.hcfactions.listener;

import java.util.concurrent.TimeUnit;

import com.cobelpvp.atheneum.util.PlayerUtils;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.commands.CustomTimerCreateCommand;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.util.InventoryUtils;
import com.cobelpvp.hcfactions.util.RegenUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class  SpawnListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions for this in safe-zone.");
        } else if (HCFactions.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || HCFactions.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())) {
            if (!DTRHCFClaim.SAFE_ZONE.appliesAt(event.getBlock().getLocation()) && event.getItemInHand() != null && event.getItemInHand().getType() == Material.WEB) {
                for (Event playableEvent : HCFactions.getInstance().getEventHandler().getEvents()) {
                    if (!playableEvent.isActive() || !(playableEvent instanceof KOTH)) {
                        continue;
                    }
                    
                    KOTH koth = (KOTH) playableEvent;
                    
                    if (koth.onCap(event.getBlockPlaced().getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't place web on cap!");
                        event.getPlayer().setItemInHand(null);
                        
                        event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(HCFactions.getInstance(), new Object()));
                        
                        Bukkit.getScheduler().runTask(HCFactions.getInstance(), () -> {
                            event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", HCFactions.getInstance());
                        });
                        
                        return;
                    }
                }
                
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (event.getBlock().getType() == Material.WEB) {
                            event.getBlock().setType(Material.AIR);
                        }
                    }

                }.runTaskLater(HCFactions.getInstance(), 10 * 20L);
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permissions for this in the close to spawn!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have permissions for this in safe-zone.");
        } else if (!DTRHCFClaim.DTC.appliesAt(event.getBlock().getLocation()) && (HCFactions.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || HCFactions.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation()))) {
            event.setCancelled(true);

            if (event.getBlock().getType() != Material.LONG_GRASS && event.getBlock().getType() != Material.GRASS) {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permissions for this in the close to spawn!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || HCFactions.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME || HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME || HCFactions.getInstance().getServerHandler().isAdminOverride((Player) event.getDamager())) {
            return;
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (HCFactions.getInstance().getServerHandler().isSpawnBufferZone(event.getBlockClicked().getLocation())) {
            if (!HCFactions.getInstance().getServerHandler().isWaterPlacementInClaimsAllowed()) {
                event.setCancelled(true);
            } else {
                final Block waterBlock = event.getBlockClicked().getRelative(event.getBlockFace());

                if (waterBlock.getRelative(BlockFace.NORTH).isLiquid() || waterBlock.getRelative(BlockFace.SOUTH).isLiquid() || waterBlock.getRelative(BlockFace.EAST).isLiquid() || waterBlock.getRelative(BlockFace.WEST).isLiquid()) {
                    event.setCancelled(true);
                    return;
                }

                RegenUtils.schedule(waterBlock, 30, TimeUnit.SECONDS, (block) -> InventoryUtils.fillBucket(event.getPlayer()), (block) -> true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if ((event.getEntity() instanceof Player || event.getEntity() instanceof Horse) && DTRHCFClaim.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        if (damager != null) {
            Player victim = (Player) event.getEntity();

            if (DTRHCFClaim.SAFE_ZONE.appliesAt(victim.getLocation()) || DTRHCFClaim.SAFE_ZONE.appliesAt(damager.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Faction faction = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
        if (faction != null && faction.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE)) {
            event.getItemDrop().remove();
        }
    }
}