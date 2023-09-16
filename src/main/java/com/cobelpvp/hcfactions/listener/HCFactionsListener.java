package com.cobelpvp.hcfactions.listener;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.commands.CustomTimerCreateCommand;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Claim;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdStuck;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.hcfactions.server.RegionData;
import com.cobelpvp.hcfactions.server.ServerHandler;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.cobelpvp.hcfactions.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@SuppressWarnings("deprecation")
public class HCFactionsListener implements Listener {

    private static final Map<BlockVector, UUID> pressurePlates = new ConcurrentHashMap<>();
    public static final ItemStack FIRST_SPAWN_BOOK = new ItemStack(WRITTEN_BOOK);
    public static final ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(FISHING_ROD);
    public static final ItemStack FIRST_SPAWN_IRON_PICKAXE = new ItemStack(IRON_PICKAXE);
    public static final ItemStack FIRST_SPAWN_IRON_SWORD = new ItemStack(IRON_SWORD);
    public static final ItemStack FIRST_SPAWN_DIAMOND_AXE = new ItemStack(DIAMOND_AXE);
    public static final ItemStack FIRST_SPAWN_DIAMOND_SPADE = new ItemStack(DIAMOND_SPADE);
    public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(LAVA_BUCKET, WATER_BUCKET, BUCKET);
    public static final Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(GLASS, WOOD_DOOR, IRON_DOOR, FENCE_GATE);
    public static final Set<Material> NO_INTERACT = ImmutableSet.of(FENCE_GATE, FURNACE, BURNING_FURNACE, BREWING_STAND, CHEST, HOPPER, DISPENSER, WOODEN_DOOR, STONE_BUTTON, WOOD_BUTTON, TRAPPED_CHEST, TRAP_DOOR, LEVER, DROPPER, ENCHANTMENT_TABLE, BED_BLOCK, ANVIL, BEACON);
    private static final List<UUID> processingTeleportPlayers = new CopyOnWriteArrayList<>();

    static {
        BookMeta bookMeta = (BookMeta) FIRST_SPAWN_BOOK.getItemMeta();

        String serverName = HCFactions.getInstance().getServerHandler().getServerName();

        bookMeta.setTitle(GOLD + "Welcome to " + serverName);
        bookMeta.setPages(

                GOLD + "Welcome to " + serverName + "!",
                "type ./f to see the faction commands."

        );
        bookMeta.setAuthor(HCFactions.getInstance().getServerHandler().getServerName());

        FIRST_SPAWN_BOOK.setItemMeta(bookMeta);
        FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
        FIRST_SPAWN_IRON_PICKAXE.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
        FIRST_SPAWN_IRON_SWORD.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
        FIRST_SPAWN_DIAMOND_AXE.addEnchantment(Enchantment.DIG_SPEED,3);
        FIRST_SPAWN_DIAMOND_SPADE.addEnchantment(Enchantment.DIG_SPEED,5);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        processTerritoryInfo(event);
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("hcf.sign.color")) {
            String[] lines = event.getLines();
            for (int i = 0; i < lines.length; ++i) {
                event.setLine(i, ColorText.translate(lines[i]));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
            HCFactions.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()).getTaskId());
            ServerHandler.getTasks().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
        }

        processTerritoryInfo(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        HCFactions.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        HCFactions.getInstance().getPlaytimeMap().playerJoined(event.getPlayer().getUniqueId());
        HCFactions.getInstance().getLastJoinMap().setLastJoin(event.getPlayer().getUniqueId());

        if (!event.getPlayer().hasPlayedBefore()) {
            HCFactions.getInstance().getFirstJoinMap().setFirstJoin(event.getPlayer().getUniqueId());
            TeamsEconomyHandler.setBalance(event.getPlayer().getUniqueId(), 100D);

                if (HCFactions.getInstance().getServerHandler().isStartingTimerEnabled()) {
                    HCFactions.getInstance().getPvPTimerMap().createStartingTimer(event.getPlayer().getUniqueId(), (int) TimeUnit.HOURS.toSeconds(1));
                } else {
                    HCFactions.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
                }

            event.getPlayer().teleport(HCFactions.getInstance().getServerHandler().getSpawnLocation());

            if (HCFactions.getInstance().getDeathsMap().getDeaths(event.getPlayer().getUniqueId()) == 0) {
                HCFactions.getInstance().getDeathsMap().setDeaths(event.getPlayer().getUniqueId(), 0);
            }

            if (HCFactions.getInstance().getKillsMap().getKills(event.getPlayer().getUniqueId()) == 0) {
                HCFactions.getInstance().getKillsMap().setKills(event.getPlayer().getUniqueId(), 0);
            }
        }
    }

    @EventHandler
    public void onBookDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(FIRST_SPAWN_BOOK)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStealthPickaxe(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
            if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
                event.setCancelled(true);

                block.breakNaturally(inHand);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStealthItemPickup(PlayerPickupItemEvent event) {
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
            if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
                event.setCancelled(true);
                event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
                event.getItem().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ServerHandler.getTasks().containsKey(player.getName())) {
                HCFactions.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()).getTaskId());
                ServerHandler.getTasks().remove(player.getName());
                player.sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        Location to = event.getLocation();

        if (to.getX() <= 400 || to.getZ() <= 400 || to.getY() <= 400) {
            if (reason == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == POTION) {
                try {
                    ItemStack i = event.getItem();

                    if (i.getDurability() != (short) 0) {
                        Potion pot = Potion.fromItemStack(i);

                        if (pot != null && pot.isSplash() && pot.getType() != null && DEBUFFS.contains(pot.getType().getEffectType())) {
                            if (HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                                player.sendMessage(RED + "You cannot do this while your PVP Timer is active!");
                                player.sendMessage(RED + "Type '" + YELLOW + "/pvp enable" + RED + "' to remove your timer.");
                                event.setCancelled(true);
                                return;
                            }

                            if (DTRHCFClaim.SAFE_ZONE.appliesAt(player.getLocation())) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(RED + "You cannot launch debuffs from inside spawn!");
                                event.getPlayer().updateInventory();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() == ENCHANTMENT_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == ENCHANTED_BOOK) {
                    event.getItem().setType(BOOK);

                    event.getPlayer().sendMessage(GREEN + "You reverted this book to its original form!");
                    event.setCancelled(true);
                }
            }

            return;
        }

        if (HCFactions.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || HCFactions.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        Faction faction = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

        if (faction != null && !faction.isMember(event.getPlayer().getUniqueId())) {
            if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                if (event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("DOOR")) {
                    CitadelHandler citadelHandler = HCFactions.getInstance().getCitadelHandler();

                    if (DTRHCFClaim.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())) {
                        return;
                    }
                }

                event.setCancelled(true);
                event.getPlayer().sendMessage(RED + "You can’t interact in the territory of " + faction.getName(event.getPlayer()));

                if (event.getMaterial() == TRAP_DOOR || event.getMaterial() == FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
                    HCFactions.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                }

                return;
            }

            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
            }
        } else if (event.getMaterial() == LAVA_BUCKET) {
            if (faction == null || !faction.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(RED + "You can only do this in your own claims!");
            }
        } else {
            UUID uuid = player.getUniqueId();

            if (faction != null && !faction.isCaptain(uuid) && !faction.isCoLeader(uuid) && !faction.isOwner(uuid)) {
                Subclaim subclaim = faction.getSubclaim(event.getClickedBlock().getLocation());

                if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                    if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(DARK_RED + "You do not have access to the subclaim " + GREEN + subclaim.getName() + DARK_RED + "!");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) event.getClickedBlock().getState();

                if (DTRHCFClaim.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                    if (s.getLine(0).contains("Kit")) {
                        HCFactions.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
                    } else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
                        HCFactions.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
                    }

                    event.setCancelled(true);
                }
            }
        }

        if (event.getItem() != null && event.getMaterial() == SIGN) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) event.getItem().getItemMeta().getLore();

                if (lore.size() > 1 && lore.get(1).contains("§e")) {
                    if (event.getClickedBlock() != null) {
                        event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(HCFactions.getInstance(), true));

                        new BukkitRunnable() {

                            public void run() {
                                event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", HCFactions.getInstance());
                            }

                        }.runTaskLater(HCFactions.getInstance(), 20L);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        ItemStack hand = e.getItemInHand();

        if (hand.getType() == SIGN) {
            if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) hand.getItemMeta().getLore();

                if (e.getBlock().getType() == WALL_SIGN || e.getBlock().getType() == SIGN_POST) {
                    Sign s = (Sign) e.getBlock().getState();

                    for (int i = 0; i < 4; i++) {
                        s.setLine(i, lore.get(i));
                    }

                    s.setMetadata("deathSign", new FixedMetadataValue(HCFactions.getInstance(), true));
                    s.update();
                }
            }
        } else if (hand.getType() == MOB_SPAWNER) {
            if (!(e.isCancelled())) {
                if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().getDisplayName().startsWith(RESET.toString())) {
                    String name = stripColor(hand.getItemMeta().getDisplayName());
                    String entName = name.replace(" Spawner", "");
                    EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));

                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    spawner.setSpawnedType(type);
                    spawner.update();

                    e.getPlayer().sendMessage(BLUE + "You placed a " + entName + " spawner!");
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == WALL_SIGN || e.getBlock().getType() == SIGN_POST) {
            if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
                e.setCancelled(true);

                Sign sign = (Sign) e.getBlock().getState();

                ItemStack deathsign = new ItemStack(SIGN);
                ItemMeta meta = deathsign.getItemMeta();

                if (sign.getLine(1).contains("Captured")) {
                    meta.setDisplayName("§cKOTH Capture Sign");
                } else {
                    meta.setDisplayName("§dDeath Sign");
                }

                meta.setLore(Arrays.asList(sign.getLines()));
                deathsign.setItemMeta(meta);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

                e.getBlock().setType(AIR);
                e.getBlock().getState().removeMetadata("deathSign", HCFactions.getInstance());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        SpawnTagHandler.removeTag(event.getEntity());
        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getEntity());
        Player killer = event.getEntity().getKiller();

        if (HCFactions.getInstance().getInDuelPredicate().test(event.getEntity())) {
            return;
        }

        if (killer != null) {
            Faction killerFaction = HCFactions.getInstance().getFactionHandler().getTeam(killer);
            Location deathLoc = event.getEntity().getLocation();
            int deathX = deathLoc.getBlockX();
            int deathY = deathLoc.getBlockY();
            int deathZ = deathLoc.getBlockZ();

            if (killerFaction != null) {
                TeamActionTracker.logActionAsync(killerFaction, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP, ImmutableMap.of("playerId", killer.getUniqueId(), "playerName", killer.getName(), "killedId", event.getEntity().getUniqueId(), "killedName", event.getEntity().getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
            }

            if (playerFaction != null) {
                TeamActionTracker.logActionAsync(playerFaction, TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP, ImmutableMap.of("playerId", event.getEntity().getUniqueId(), "playerName", event.getEntity().getName(), "killerId", killer.getUniqueId(), "killerName", killer.getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
            }

            if (!event.getEntity().equals(killer)) {
                ItemStack hand = killer.getItemInHand();

                if (hand.getType().name().contains("SWORD") || hand.getType() == BOW) {
                    InventoryUtils.addKill(hand, killer.getDisplayName() + YELLOW + " " + (hand.getType() == BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
                }
            }
        }

        if (playerFaction != null) {
            playerFaction.playerDeath(event.getEntity().getName(), HCFactions.getInstance().getServerHandler().getDTRLoss(event.getEntity()));
        }

        if (killer == null || (!event.getEntity().equals(killer))) {
            String deathMsg = YELLOW + event.getEntity().getName() + RESET + " " + (event.getEntity().getKiller() != null ? "killed by " + YELLOW + event.getEntity().getKiller().getName() : "died") + " " + GOLD + InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

            for (ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
                if (armor != null && armor.getType() != AIR) {
                    InventoryUtils.addDeath(armor, deathMsg);
                }
            }
        }

        event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

        double bal = 100;
        if (event.getEntity().getKiller() != null && !Double.isNaN(bal) && bal > 0) {
            TeamsEconomyHandler.deposit(event.getEntity().getKiller().getUniqueId(), bal);
            event.getEntity().getKiller().sendMessage(GREEN + "You earned " + BOLD + "$" + bal + GREEN + " for killing " + event.getEntity().getDisplayName() + GREEN + "!");
        }
    }

    private void processTerritoryInfo(PlayerMoveEvent event) {
        Faction ownerTo = LandBoard.getInstance().getTeam(event.getTo());

        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            if (!DTRHCFClaim.SAFE_ZONE.appliesAt(event.getTo())) {

                if (DTRHCFClaim.KOTH.appliesAt(event.getTo()) || DTRHCFClaim.CITADEL.appliesAt(event.getTo())) {
                    HCFactions.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());

                    event.getPlayer().sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");
                } else if (ownerTo != null && ownerTo.getOwner() != null) {
                    if (!ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);

                        for (Claim claim : ownerTo.getClaims()) {
                            if (claim.contains(event.getFrom()) && !ownerTo.isMember(event.getPlayer().getUniqueId())) {
                                Location nearest = CmdStuck.nearestSafeLocation(event.getPlayer().getLocation());
                                boolean spawn = false;

                                if (nearest == null) {
                                    nearest = HCFactions.getInstance().getServerHandler().getSpawnLocation();
                                    spawn = true;
                                }

                                event.getPlayer().teleport(nearest);
                                event.getPlayer().sendMessage(ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed.");
                                return;
                            }
                        }

                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another faction's territory with PvP Protection.");
                        event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
                        return;
                    }
                }
            }
        }

        Faction ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());

        if (ownerFrom != ownerTo) {
            ServerHandler sm = HCFactions.getInstance().getServerHandler();
            RegionData from = sm.getRegion(ownerFrom, event.getFrom());
            RegionData to = sm.getRegion(ownerTo, event.getTo());

            if (from.equals(to)) return;

            if (!to.getRegionType().getMoveHandler().handleMove(event)) {
                return;
            }

            event.getPlayer().sendMessage(ColorText.translate("&eLeaving " + from.getName(event.getPlayer()) + "&e, entering " + to.getName(event.getPlayer())));
        }
    }

}
