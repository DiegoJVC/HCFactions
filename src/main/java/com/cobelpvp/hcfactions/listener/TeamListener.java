package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.GlowHandler;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.factions.event.PlayerBuildInOthersClaimEvent;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.hcfactions.util.InventoryUtils;
import com.cobelpvp.hcfactions.util.RegenUtils;
import com.google.common.collect.ImmutableMap;
import com.cobelpvp.atheneum.util.PlayerUtils;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeamListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

        if (faction != null && faction.getMaxOnline() > 0 && faction.getOnlineMemberAmount() >= faction.getMaxOnline()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.BLUE + "Your faction currently has too many players logged in. Try again later!");
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

        if (faction != null) {
            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (faction.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GOLD + "Member online: " + ChatColor.DARK_GREEN + event.getPlayer().getName());
                } else if (faction.getAllies().size() != 0 && faction.isAlly(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GOLD + "Ally online: " + ChatColor.BLUE + event.getPlayer().getName());
                }
            }

            TeamActionTracker.logActionAsync(faction, TeamActionType.MEMBER_CONNECTED, ImmutableMap.of("playerId", event.getPlayer().getUniqueId(), "playerName", event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());

        if (faction != null) {
            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (player.equals(event.getPlayer())) {
                    continue;
                }

                if (faction.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GOLD + "Member offline: " + ChatColor.DARK_RED + event.getPlayer().getName());
                } else if (faction.isAlly(player.getUniqueId())) {
                    player.sendMessage(ChatColor.GOLD + "Ally Offline: " + ChatColor.RED + event.getPlayer().getName());
                }
            }

            TeamActionTracker.logActionAsync(faction, TeamActionType.MEMBER_DISCONNECTED, ImmutableMap.of(
                    "playerId", event.getPlayer().getUniqueId(),
                    "playerName", event.getPlayer().getName()
            ));
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            Faction owner = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

            if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && owner.isMember(event.getPlayer().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (!faction.isMember(event.getPlayer().getUniqueId())) {
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
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't destroy in the territory of " + faction.getName(event.getPlayer()));
                event.setCancelled(true);
            }
            return;
        }

        if (!faction.isCoLeader(event.getPlayer().getUniqueId()) && !faction.isCaptain(event.getPlayer().getUniqueId()) && !faction.isOwner(event.getPlayer().getUniqueId())) {
            Subclaim subclaim = faction.getSubclaim(event.getBlock().getLocation());

            if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have access to the subclaim " + subclaim.getName() + ChatColor.DARK_RED  + "!");
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (event.getBlock().getType() == Material.GLOWSTONE && HCFactions.getInstance().getGlowHandler().hasGlowMountain() && faction.getName().equals(GlowHandler.getGlowTeamName())) {
            return;
        }

        if (faction.hasDTRHCFClaim(DTRHCFClaim.ROAD) && event.getBlock().getY() <= 40) {
            return;
        }

        if (!faction.isMember(event.getPlayer().getUniqueId())) {
            PlayerBuildInOthersClaimEvent buildEvent = new PlayerBuildInOthersClaimEvent(event.getPlayer(), event.getBlock(), faction);
            Bukkit.getPluginManager().callEvent(buildEvent);

            if (buildEvent.isWillIgnore()) {
                return;
            }
            
            event.setCancelled(true);

            if (!HCFactionsListener.ATTACK_DISABLING_BLOCKS.contains(event.getBlock().getType())) {
                if (event.getBlock().isEmpty() || event.getBlock().getType().isTransparent() || !event.getBlock().getType().isSolid()) {
                    return;
                }
            }

            HCFactions.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
            return;
        }

        if (!faction.isCoLeader(event.getPlayer().getUniqueId()) && !faction.isCaptain(event.getPlayer().getUniqueId()) && !faction.isOwner(event.getPlayer().getUniqueId())) {
            Subclaim subclaim = faction.getSubclaim(event.getBlock().getLocation());

            if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You do not have access to the subclaim " + subclaim.getName() + ChatColor.DARK_RED  + "!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }

        Block retractBlock = event.getRetractLocation().getBlock();

        if (retractBlock.isEmpty() || retractBlock.isLiquid()) {
            return;
        }

        Faction pistonFaction = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        Faction targetFaction = LandBoard.getInstance().getTeam(retractBlock.getLocation());

        if (pistonFaction == targetFaction) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Faction pistonFaction = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        int i = 0;

        for (Block block : event.getBlocks()) {
            i++;

            Block targetBlock = event.getBlock().getRelative(event.getDirection(), i + 1);
            Faction targetFaction = LandBoard.getInstance().getTeam(targetBlock.getLocation());

            if (targetFaction == pistonFaction || targetFaction == null || targetFaction.isRaidable()) {
                continue;
            }

            if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (!faction.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || HCFactions.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (!faction.isMember(event.getRemover().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME || HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getRightClicked().getLocation())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getRightClicked().getLocation());

        if (!faction.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) {
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

        if (damager == null || HCFactions.getInstance().getServerHandler().isAdminOverride(damager) || HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (!faction.isMember(event.getDamager().getUniqueId())) {
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
            Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(damager);
            Player victim = (Player) event.getEntity();

            if (faction != null && event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                if (faction.isMember(victim.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityHorseDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Horse)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        Horse victim = (Horse) event.getEntity();

        if (damager != null && victim.isTamed()) {
            Faction damagerFaction = HCFactions.getInstance().getFactionHandler().getTeam(damager);
            UUID horseOwner = victim.getOwner().getUniqueId();

            if(!damager.getUniqueId().equals(horseOwner) && damagerFaction != null && damagerFaction.isMember(horseOwner)) {
                event.setCancelled(true);
                damager.sendMessage(ChatColor.YELLOW + "This horse belongs to " + ChatColor.DARK_GREEN + TeamsUUIDCache.name(horseOwner) + ChatColor.YELLOW + " who is in your faction.");
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(checkLocation)) {
            return;
        }

        Faction owner = LandBoard.getInstance().getTeam(checkLocation);

        boolean canPlace = owner.hasDTRHCFClaim(DTRHCFClaim.KOTH) && HCFactions.getInstance().getServerHandler().isWaterPlacementInClaimsAllowed();

        if (!owner.isMember(event.getPlayer().getUniqueId())) {
            if (!canPlace) {
                event.setCancelled(true);
                if (owner.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE)) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't destroy in a safe-zone.");
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
                }

                if (!(owner == null)) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't destroy in the territory of " + owner.getName());
                }
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

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

        if (HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(checkLocation)) {
            return;
        }

        Faction owner = LandBoard.getInstance().getTeam(checkLocation);

        if (!owner.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            if (owner.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE)) {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't destroy in a safe-zone.");
            } else {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission for this area.");
            }

            if (!(owner == null)) {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't destroy in the territory of " + owner.getName());
            }
        }
    }
}