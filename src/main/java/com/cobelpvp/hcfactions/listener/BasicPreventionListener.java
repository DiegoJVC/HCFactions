package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.commands.CustomTimerCreateCommand;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class BasicPreventionListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Wither) {
            event.setCancelled(true);
        }

        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/kill") || event.getMessage().toLowerCase().startsWith("/slay") || event.getMessage().toLowerCase().startsWith("/bukkit:kill") || event.getMessage().toLowerCase().startsWith("/bukkit:slay") || event.getMessage().toLowerCase().startsWith("/suicide")) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
            Horse horse = (Horse) event.getVehicle();
            Player player = (Player) event.getEntered();

            if (horse.getOwner() != null && !horse.getOwner().getName().equals(player.getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This is not your horse!");
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getEntity().getLocation()) && event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            event.setCancelled(true);
            return;
        }

        if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            if (Atheneum.RANDOM.nextInt(100) > (HCFactions.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId()) ? 10 : 30)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!HCFactions.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HCFactions.getInstance(), () -> {
                HCFactions.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), 30 * 60);
            }, 20L);
        }
        event.setRespawnLocation(HCFactions.getInstance().getServerHandler().getSpawnLocation());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (HCFactions.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER && (event.getBlock().getType() == Material.BED || event.getBlock().getType() == Material.BED_BLOCK)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place beds in the Nether.");
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent event) {
        if (HCFactions.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        EntityType type = event.getEntityType();

        if (type == EntityType.MINECART_TNT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH) 
    public void onDamageByEntity(EntityDamageByEntityEvent event) {

        Entity damaged = event.getEntity();

        if (!(damaged instanceof Player)) {
            return;
        }
    }
}
