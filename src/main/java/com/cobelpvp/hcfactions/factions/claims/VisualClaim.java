package com.cobelpvp.hcfactions.factions.claims;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.*;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdClaim;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdResize;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.hcfactions.util.CuboidRegion;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class VisualClaim implements Listener {

    public static final int MAP_RADIUS = 50;
    public static final Material[] MAP_MATERIALS = {Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD,
            Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST,
            Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE,
            Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE,
            Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE};
    public static final BlockFace[] NESW_BLOCKS = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @Getter
    private static Map<String, VisualClaim> currentMaps = new HashMap<>();
    @Getter
    private static Map<String, VisualClaim> currentSubclaimMaps = new HashMap<>();
    @Getter
    private static Map<String, VisualClaim> visualClaims = new HashMap<>();
    private static boolean taskSetup = false;
    private static Map<UUID, Queue<QueuedBlockChange>> queuedBlockChanges = Maps.newHashMap();
    @Getter
    @NonNull
    private Player player;
    @Getter
    @NonNull
    private VisualClaimType type;
    @Getter
    @NonNull
    private boolean bypass;
    @Getter
    private List<Location> blockChanges = new ArrayList<>();
    @Getter
    @Setter
    private Claim resizing;
    @Getter
    @Setter
    private Location corner1;
    @Getter
    @Setter
    private Location corner2;

    private static void checkTaskSetup() {
        if (taskSetup) {
            return;
        }

        taskSetup = true;
        Bukkit.getScheduler().runTaskTimer(HCFactions.getInstance(), () -> {
            Iterator<Entry<UUID, Queue<QueuedBlockChange>>> entryIterator = queuedBlockChanges.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Entry<UUID, Queue<QueuedBlockChange>> entry = entryIterator.next();

                Player bukkitPlayer = Bukkit.getPlayer(entry.getKey());
                if (bukkitPlayer == null) {
                    entryIterator.remove();
                    continue;
                }

                Queue<QueuedBlockChange> queue = entry.getValue();
                QueuedBlockChange queuedBlockChange = queue.poll();
                if (queuedBlockChange == null) {
                    entryIterator.remove();
                    continue;
                }

                bukkitPlayer.sendBlockChange(queuedBlockChange.getLocation(), queuedBlockChange.getType(), queuedBlockChange.getData());
            }
        }, 1L, 1L);
    }

    private static void sendBlockChange(Player player, Location location, Material type, byte data) {

        if (true) {
            player.sendBlockChange(location, type, data);
            return;
        }

        if (!queuedBlockChanges.containsKey(player.getUniqueId())) {
            queuedBlockChanges.put(player.getUniqueId(), new ConcurrentLinkedQueue<>());
        }

        queuedBlockChanges.get(player.getUniqueId()).add(new QueuedBlockChange(location, type, data));
    }

    public static VisualClaim getVisualClaim(String name) {
        return (visualClaims.get(name));
    }

    public void draw(boolean silent) {
        checkTaskSetup();
        if (currentMaps.containsKey(player.getName()) && (type == VisualClaimType.MAP || type == VisualClaimType.SURFACE_MAP)) {
            currentMaps.get(player.getName()).cancel();

            if (!silent) {
                if (type == VisualClaimType.MAP) {
                    player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "The surface map has been hidden!");
                }
            }

            return;
        } else if (currentSubclaimMaps.containsKey(player.getName()) && type == VisualClaimType.SUBCLAIM_MAP) {
            currentSubclaimMaps.get(player.getName()).cancel();

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Subclaims have been hidden!");
            }

            return;
        }

        if (visualClaims.containsKey(player.getName()) && !(type == VisualClaimType.MAP || type == VisualClaimType.SUBCLAIM_MAP || type == VisualClaimType.SURFACE_MAP)) {
            visualClaims.get(player.getName()).cancel();
        }

        switch (type) {
            case MAP:
            case SURFACE_MAP:
                currentMaps.put(player.getName(), this);
                break;
            case SUBCLAIM_MAP:
                currentSubclaimMaps.put(player.getName(), this);
                break;
            default:
                visualClaims.put(player.getName(), this);
                break;
        }

        HCFactions.getInstance().getServer().getPluginManager().registerEvents(this, HCFactions.getInstance());

        switch (type) {
            case CREATE:
                player.sendMessage(ChatColor.GOLD + "Faction land claim started.");
                player.sendMessage(ChatColor.YELLOW + "Left click at a corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Right click on the second corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.YELLOW + "Crouch left click the air to purchase your claim.");
                break;
            case RESIZE:
                player.sendMessage(ChatColor.GOLD + "Faction land resize started.");
                player.sendMessage(ChatColor.YELLOW + "Left click in the claim you'd like to resize.");
                player.sendMessage(ChatColor.YELLOW + "Right click on the corner you'd like to resize to.");
                player.sendMessage(ChatColor.YELLOW + "Crouch left click the air to confirm your resize.");
                break;
            case MAP:
                int claimIteration = 0;
                Map<Map.Entry<Claim, Faction>, Material> sendMaps = new HashMap<>();

                for (Map.Entry<Claim, Faction> regionData : LandBoard.getInstance().getRegionData(player.getLocation(), MAP_RADIUS, 256, MAP_RADIUS)) {
                    Material mat = getMaterial(claimIteration);
                    claimIteration++;

                    drawClaim(regionData.getKey(), mat);
                    sendMaps.put(regionData, mat);
                }

                if (sendMaps.isEmpty()) {
                    if (!silent) {
                        player.sendMessage(ChatColor.YELLOW + "There are no claims within " + MAP_RADIUS + " blocks of you!");
                    }

                    cancel();
                }

                if (!silent) {
                    for (Map.Entry<Map.Entry<Claim, Faction>, Material> mapEntry : sendMaps.entrySet()) {
                        Faction faction = mapEntry.getKey().getValue();
                        Claim claim = mapEntry.getKey().getKey();

                        if (faction.getOwner() == null) {
                            player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + faction.getName(player) + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + faction.getName(player));
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + claim.getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + faction.getName());
                        }
                    }
                }

                break;
            case SURFACE_MAP:
                for (Map.Entry<Claim, Faction> regionData : LandBoard.getInstance().getRegionData(player.getLocation(), MAP_RADIUS, 256, MAP_RADIUS)) {
                    Claim claim = regionData.getKey();
                    Faction claimOwner = regionData.getValue();
                    World claimWorld = HCFactions.getInstance().getServer().getWorld(claim.getWorld());

                    for (Coordinate coordinate : claim) {
                        Block block = claimWorld.getBlockAt(coordinate.getX(), 100, coordinate.getZ());
                        boolean displayCarpet = false;

                        for (BlockFace blockFace : NESW_BLOCKS) {
                            Location relative = block.getRelative(blockFace).getLocation();

                            if (!claimOwner.ownsLocation(relative)) {
                                displayCarpet = true;
                                break;
                            }
                        }

                        if (!displayCarpet) {
                            continue;
                        }

                        while (!block.getType().isSolid() || block.getType() == Material.LEAVES || block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                            block = block.getRelative(BlockFace.DOWN);
                        }

                        DyeColor carpetColor;

                        if (claimOwner.getOwner() != null) {
                            if (claimOwner.isMember(player.getUniqueId())) {
                                carpetColor = DyeColor.GREEN;
                            } else if (claimOwner.isAlly(player.getUniqueId())) {
                                carpetColor = DyeColor.BLUE;
                            } else {
                                carpetColor = DyeColor.RED;
                            }
                        } else if (claimOwner.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE)) {
                            carpetColor = DyeColor.LIME;
                        } else {
                            carpetColor = DyeColor.BLUE;
                        }

                        sendBlockChange(player, block.getLocation(), Material.WOOL, carpetColor.getWoolData());
                        sendBlockChange(player, block.getRelative(BlockFace.UP).getLocation(), Material.CARPET, carpetColor.getWoolData());
                        blockChanges.add(block.getLocation());
                        blockChanges.add(block.getRelative(BlockFace.UP).getLocation());
                    }
                }

                if (blockChanges.size() == 0) {
                    if (!silent) {
                        player.sendMessage(ChatColor.YELLOW + "There are no claims near you!");
                    }

                    cancel();
                    return;
                }

                if (!silent) {
                    player.sendMessage(ChatColor.YELLOW + "Claims have been shown.");
                }

                break;
            case SUBCLAIM_MAP:
                Faction senderFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

                if (bypass) {
                    senderFaction = LandBoard.getInstance().getTeam(player.getLocation());
                }

                if (senderFaction == null) {
                    if (!silent) {
                        player.sendMessage(ChatColor.RED + "You must be on a faction to view subclaims.");
                    }

                    cancel();
                    return;
                }

                int subclaimIteration = 0;
                Map<Subclaim, Material> subclaimMaterialMap = new HashMap<>();

                for (Subclaim subclaim : senderFaction.getSubclaims()) {
                    if (subclaim.getLoc1().distanceSquared(player.getLocation()) > MAP_RADIUS * MAP_RADIUS && subclaim.getLoc2().distanceSquared(player.getLocation()) > MAP_RADIUS * MAP_RADIUS) {
                        continue;
                    }

                    Material mat = getMaterial(subclaimIteration);
                    subclaimIteration++;

                    subclaimMaterialMap.put(subclaim, mat);
                    drawSubclaim(subclaim, mat);
                }

                if (subclaimIteration == 0) {
                    if (!silent) {
                        player.sendMessage(ChatColor.YELLOW + "There are no subclaims within " + MAP_RADIUS + " blocks of you!");
                    }

                    cancel();
                    return;
                }

                if (!silent) {
                    for (Map.Entry<Subclaim, Material> entry : subclaimMaterialMap.entrySet()) {
                        player.sendMessage(ChatColor.YELLOW + "Subclaim " + ChatColor.BLUE + entry.getKey().getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(entry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + senderFaction.getName());
                    }
                }

                break;
        }
    }

    public boolean containsOtherClaim(Claim claim) {
        Location maxPoint = claim.getMaximumPoint();
        Location minPoint = claim.getMinimumPoint();
        Faction maxFaction = LandBoard.getInstance().getTeam(maxPoint);

        if (maxFaction != null && (type != VisualClaimType.RESIZE || !maxFaction.isMember(player.getUniqueId()))) {
            return (true);
        }

        Faction minFaction = LandBoard.getInstance().getTeam(minPoint);

        if (minFaction != null && (type != VisualClaimType.RESIZE || !minFaction.isMember(player.getUniqueId()))) {
            return (true);
        }

        if (Math.abs(claim.getX1() - claim.getX2()) == 0 || Math.abs(claim.getZ1() - claim.getZ2()) == 0) {
            return (false);
        }

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                Location at = new Location(HCFactions.getInstance().getServer().getWorld(claim.getWorld()), x, 80, z);
                Faction factionAt = LandBoard.getInstance().getTeam(at);

                if (factionAt != null && (type != VisualClaimType.RESIZE || !factionAt.isMember(player.getUniqueId()))) {
                    return (true);
                }
            }
        }

        return (false);
    }

    public Set<Claim> getTouchingClaims(Claim claim) {
        Set<Claim> touchingClaims = new HashSet<>();

        for (Coordinate coordinate : claim.outset(Claim.CuboidDirection.Horizontal, 1)) {
            Location loc = new Location(HCFactions.getInstance().getServer().getWorld(claim.getWorld()), coordinate.getX(), 80, coordinate.getZ());
            Map.Entry<Claim, Faction> claimAtLocation = LandBoard.getInstance().getRegionData(loc);

            if (claimAtLocation != null) {
                touchingClaims.add(claimAtLocation.getKey());
            }
        }

        return (touchingClaims);
    }

    public void setLoc(int locationId, final Location clicked) {
        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a faction to " + type.name().toLowerCase() + " land!");
            cancel();
            return;
        }

        if (type == VisualClaimType.CREATE) {
            if (!bypass && !HCFactions.getInstance().getServerHandler().isUnclaimed(clicked)) {
                player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                return;
            }

            if (locationId == 1) {
                if (corner2 != null && isIllegalClaim(new Claim(clicked, corner2), null)) {
                    return;
                }

                clearPillarAt(corner1);
                this.corner1 = clicked;
            } else if (locationId == 2) {
                if (corner1 != null && isIllegalClaim(new Claim(corner1, clicked), null)) {
                    return;
                }

                clearPillarAt(corner2);
                this.corner2 = clicked;
            }

            HCFactions.getInstance().getServer().getScheduler().runTaskLater(HCFactions.getInstance(), new BukkitRunnable() {

                @Override
                public void run() {
                    erectPillar(clicked, Material.EMERALD_BLOCK);
                }

            }, 1L);

            player.sendMessage(ChatColor.YELLOW + "Set claim's location " + ChatColor.LIGHT_PURPLE + locationId + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");

            if (corner1 != null && corner2 != null) {
                int price = Claim.getPrice(new Claim(corner1, corner2), playerFaction, true);

                int x = Math.abs(corner1.getBlockX() - corner2.getBlockX());
                int z = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());

                if (price > playerFaction.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.RED + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.GREEN + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                }
            }
        } else if (type == VisualClaimType.RESIZE) {
            Map.Entry<Claim, Faction> teamAtLocation = LandBoard.getInstance().getRegionData(clicked);

            if (locationId == 1) {
                if (teamAtLocation == null || !teamAtLocation.getValue().isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "To resize your claim, please left click in the claim you'd like to resize.");
                    return;
                }

                resizing = teamAtLocation.getKey();
                drawClaim(resizing, Material.LAPIS_BLOCK);
            } else if (locationId == 2) {
                if (resizing == null) {
                    player.sendMessage(ChatColor.YELLOW + "Before you set the location you'd like to resize to, first left click in the claim you'd like to resize.");
                    return;
                }

                final Claim claimClone = new Claim(resizing);

                applyResize(claimClone, clicked);

                if (isIllegalClaim(claimClone, Arrays.asList(resizing, claimClone))) {
                    return;
                }

                this.corner2 = clicked;

                new BukkitRunnable() {

                    public void run() {
                        clearAllBlocks();
                        drawClaim(resizing, Material.LAPIS_BLOCK);
                        drawClaim(claimClone, Material.EMERALD_BLOCK);
                    }

                }.runTaskLater(HCFactions.getInstance(), 1L);
            }

            if (locationId == 1) {
                player.sendMessage(ChatColor.YELLOW + "Selected claim " + ChatColor.LIGHT_PURPLE + teamAtLocation.getKey().getName() + ChatColor.YELLOW + " to resize.");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Set resize location to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
            }

            if (resizing != null && corner2 != null) {
                int oldPrice = Claim.getPrice(resizing, null, false);
                Claim preview = new Claim(resizing);

                applyResize(preview, corner2);

                int newPrice = Claim.getPrice(preview, null, false);
                int cost = newPrice - oldPrice;

                if (cost > playerFaction.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.RED + "$" + cost);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.GREEN + "$" + cost);
                }
            }
        }
    }

    public void cancel() {
        if (type == VisualClaimType.CREATE || type == VisualClaimType.RESIZE) {
            player.getInventory().remove(CmdClaim.SELECTION_WAND);
            player.getInventory().remove(CmdResize.SELECTION_WAND);
        }

        HandlerList.unregisterAll(this);

        switch (type) {
            case MAP:
            case SURFACE_MAP:
                currentMaps.remove(player.getName());
                break;
            case SUBCLAIM_MAP:
                currentSubclaimMaps.remove(player.getName());
                break;
            default:
                visualClaims.remove(player.getName());
                break;
        }

        clearAllBlocks();
    }

    public void clearAllBlocks() {
        for (Location location : blockChanges) {
            sendBlockChange(player, location, location.getBlock().getType(), location.getBlock().getData());
        }
    }

    public void purchaseClaim() {
        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a faction to claim land!");
            cancel();
            return;
        }

        if (corner1 != null && corner2 != null) {
            int price = Claim.getPrice(new Claim(corner1, corner2), playerFaction, true);

            if (!bypass) {
                if (playerFaction.getClaims().size() >= Faction.MAX_CLAIMS) {
                    player.sendMessage(ChatColor.RED + "Your faction has the maximum amount of claims, which is " + Faction.MAX_CLAIMS + ".");
                    return;
                }

                if (!playerFaction.isCaptain(player.getUniqueId()) && !playerFaction.isCoLeader(player.getUniqueId()) && !playerFaction.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only faction captains can claim land.");
                    return;
                }

                if (playerFaction.getBalance() < price) {
                    player.sendMessage(ChatColor.RED + "Your faction does not have enough money to do this!");
                    return;
                }

                if (playerFaction.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                    return;
                }
            }

            Claim claim = new Claim(corner1, corner2);

            if (isIllegalClaim(claim, null) || Double.isNaN(playerFaction.getBalance())) {
                return;
            }

            claim.setName(playerFaction.getName() + "_" + (100 + Atheneum.RANDOM.nextInt(800)));
            claim.setY1(0);
            claim.setY2(256);

            LandBoard.getInstance().setTeamAt(claim, playerFaction);
            playerFaction.getClaims().add(claim);
            playerFaction.flagForSave();
            playerFaction.recalculateSpawnersInClaims();

            player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your faction!");

            if (!bypass) {
                playerFaction.setBalance(playerFaction.getBalance() - price);
                HCFactions.getInstance().getLogger().info("Economy Logger: Withdrawing " + price + " from " + playerFaction.getBalance() + "'s account: Claimed land");
                player.sendMessage(ChatColor.YELLOW + "Your faction's new balance is " + ChatColor.WHITE + "$" + (int) playerFaction.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + price + ")");
            }

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamActionTracker.logActionAsync(playerFaction, TeamActionType.PLAYER_CLAIM_LAND, ImmutableMap.of(
                    "playerId", player.getUniqueId(),
                    "playerName", playerFaction.getName(),
                    "cost", price,
                    "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            cancel();

            new BukkitRunnable() {

                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(HCFactions.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public void resizeClaim() {
        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (playerFaction == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a faction to resize land!");
            cancel();
            return;
        }

        if (resizing != null && corner2 != null) {
            Claim newClaim = new Claim(resizing);
            applyResize(newClaim, corner2);

            int oldPrice = Claim.getPrice(resizing, null, false);
            int newPrice = Claim.getPrice(newClaim, null, false);
            int cost = newPrice - oldPrice;

            if (!bypass) {
                if (!playerFaction.isCaptain(player.getUniqueId()) && !playerFaction.isCoLeader(player.getUniqueId()) && !playerFaction.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only faction captains can resize land.");
                    return;
                }

                if (playerFaction.getBalance() < cost) {
                    player.sendMessage(ChatColor.RED + "Your faction does not have enough money to do this!");
                    return;
                }

                if (playerFaction.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot resize land while raidable.");
                    return;
                }
            }

            if (isIllegalClaim(newClaim, null)) {
                return;
            }

            LandBoard.getInstance().setTeamAt(resizing, null);
            LandBoard.getInstance().setTeamAt(newClaim, playerFaction);
            playerFaction.getClaims().remove(resizing);
            playerFaction.getClaims().add(newClaim);
            playerFaction.flagForSave();
            playerFaction.recalculateSpawnersInClaims();

            player.sendMessage(ChatColor.YELLOW + "You have resized this land!");

            if (!bypass) {
                playerFaction.setBalance(playerFaction.getBalance() - cost);
                player.sendMessage(ChatColor.YELLOW + "Your faction's new balance is " + ChatColor.WHITE + "$" + (int) playerFaction.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + cost + ")");
            }

            Location minLoc = resizing.getMinimumPoint();
            Location maxLoc = resizing.getMaximumPoint();

            TeamActionTracker.logActionAsync(playerFaction, TeamActionType.PLAYER_RESIZE_LAND, ImmutableMap.of(
                    "playerId", player.getUniqueId(),
                    "playerName", playerFaction.getName(),
                    "cost", cost,
                    "newPoint1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "newPoint2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            cancel();

            new BukkitRunnable() {

                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(HCFactions.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    private void drawClaim(Claim claim, Material material) {
        for (Location loc : claim.getCornerLocations()) {
            erectPillar(loc, material);
        }
    }

    private void drawSubclaim(Subclaim subclaim, Material material) {
        CuboidRegion cuboidRegion = new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2());
        int glassIteration = 0;

        for (Location location : cuboidRegion) {
            int matches = 0;

            if (location.getBlockX() == cuboidRegion.getMinimumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockX() == cuboidRegion.getMaximumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMinimumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMaximumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMinimumPoint().getBlockZ()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMaximumPoint().getBlockZ()) {
                matches++;
            }

            if (matches >= 2) {
                if (glassIteration++ % 3 == 0) {
                    sendBlockChange(player, location, material, (byte) 0);
                } else {
                    sendBlockChange(player, location, Material.GLASS, (byte) 0);
                }

                blockChanges.add(location.clone());
            }
        }
    }

    private void erectPillar(Location loc, Material mat) {
        Location set = loc.clone();

        for (int y = 0; y < 256; y++) {
            set.setY(y);

            if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent() || set.getBlock().getType() == Material.WATER || set.getBlock().getType() == Material.STATIONARY_WATER) {
                if (y % 5 == 0) {
                    sendBlockChange(player, set, mat, (byte) 0);
                } else {
                    sendBlockChange(player, set, Material.GLASS, (byte) 0);
                }

                blockChanges.add(set.clone());
            }
        }
    }

    private void clearPillarAt(Location location) {
        if (location == null) {
            return;
        }

        Iterator<Location> blockChangeIterator = blockChanges.iterator();

        while (blockChangeIterator.hasNext()) {
            Location blockChange = blockChangeIterator.next();

            if (blockChange.getBlockX() == location.getBlockX() && blockChange.getBlockZ() == location.getBlockZ()) {
                sendBlockChange(player, blockChange, blockChange.getBlock().getType(), blockChange.getBlock().getData());
                blockChangeIterator.remove();
            }
        }
    }

    public boolean isIllegalClaim(Claim claim, List<Claim> ignoreNearby) {
        if (bypass) {
            return (false);
        }

        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (containsOtherClaim(claim)) {
            player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
            return (true);
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
            return (true);
        }

        Set<Claim> touchingClaims = getTouchingClaims(claim);
        Iterator<Claim> teamClaims = touchingClaims.iterator();
        boolean removedSelfClaims = false;

        while (teamClaims.hasNext()) {
            Claim possibleClaim = teamClaims.next();

            if (ignoreNearby != null && ignoreNearby.contains(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            } else if (playerFaction.ownsClaim(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            }
        }

        if (playerFaction.getClaims().size() != (type == VisualClaimType.RESIZE ? 1 : 0) && !removedSelfClaims) {
            player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
            return (true);
        }

        if (touchingClaims.size() > 1 || (touchingClaims.size() == 1 && !removedSelfClaims)) {
            player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
            return (true);
        }

        int x = Math.abs(claim.getX1() - claim.getX2());
        int z = Math.abs(claim.getZ1() - claim.getZ2());

        if (x < 5 || z < 5) {
            player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least 5 x 5!");
            return (true);
        }

        if (x > 3 * z || z > 3 * x) {
            player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
            return (true);
        }

        return (false);
    }

    public void applyResize(Claim claim, Location location) {
        double furthestDistance = 0D;
        Location furthestCorner = null;

        for (Location corner : claim.getCornerLocations()) {
            double distance = location.distanceSquared(corner);

            if (furthestCorner == null || distance > furthestDistance) {
                furthestDistance = distance;
                furthestCorner = corner;
            }
        }

        claim.setLocations(location, furthestCorner);
        claim.setY1(0);
        claim.setY2(256);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == player && player.getItemInHand() != null) {
            if (player.getItemInHand().getType() == CmdClaim.SELECTION_WAND.getType() && type == VisualClaimType.CREATE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the claiming process.");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        }

                        break;
                }

                event.setCancelled(true);
            } else if (player.getItemInHand().getType() == CmdResize.SELECTION_WAND.getType() && type == VisualClaimType.RESIZE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the resizing process.");

                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            resizeClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            resizeClaim();
                        }

                        break;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (player == event.getPlayer()) {
            cancel();
        }
    }

    public Material getMaterial(int iteration) {
        if (iteration == -1) {
            return (Material.IRON_BLOCK);
        }

        while (iteration >= MAP_MATERIALS.length) {
            iteration = iteration - MAP_MATERIALS.length;
        }

        return (MAP_MATERIALS[iteration]);
    }

    @AllArgsConstructor
    @Getter
    private static class QueuedBlockChange {
        private Location location;
        private Material type;
        private byte data;
    }

}