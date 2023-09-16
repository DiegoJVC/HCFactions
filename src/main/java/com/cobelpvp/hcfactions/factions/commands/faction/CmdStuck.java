package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.Lists;
import lombok.Getter;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CmdStuck implements Listener {

    private static final double MAX_DISTANCE = 5;

    private static final Set<Integer> warn = new HashSet<>();
    @Getter
    private static Map<String, Long> warping = new ConcurrentHashMap<>();
    private static List<String> damaged = Lists.newArrayList();

    static {
        warn.add(300);
        warn.add(270);
        warn.add(240);
        warn.add(210);
        warn.add(180);
        warn.add(150);
        warn.add(120);
        warn.add(90);
        warn.add(60);
        warn.add(30);
        warn.add(10);
        warn.add(5);
        warn.add(4);
        warn.add(3);
        warn.add(2);
        warn.add(1);

        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new CmdStuck(), HCFactions.getInstance());
    }

    @Command(names = {"f stuck", "faction stuck", "fac stuck", "stuck", "f unstuck", "faction unstuck", "fac unstuck", "unstuck"}, permission = "")
    public static void teamStuck(final Player sender) {
        if (warping.containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.YELLOW + "You are in the process of being teleported, please wait!");
            return;
        }

        if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return;
        }

        int seconds = 1;
        warping.put(sender.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(seconds));

        sender.sendMessage(ChatColor.GOLD + "Teleportation will commence in " + ChatColor.RED + "1 minutes" + ChatColor.GOLD + ". Don't move.");

        new BukkitRunnable() {

            private int seconds = 60;

            private Location loc = sender.getLocation();

            private int xStart = (int) loc.getX();
            private int yStart = (int) loc.getY();
            private int zStart = (int) loc.getZ();

            private Location nearest;

            @Override
            public void run() {
                if (damaged.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You took damage in the 1 minute grace period. " + "The teleportation request has been cancelled.");
                    damaged.remove(sender.getName());
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (!sender.isOnline()) {
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (seconds == 5) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            nearest = nearestSafeLocation(sender.getLocation());
                        }

                    }.runTask(HCFactions.getInstance());
                }

                Location loc = sender.getLocation();

                if (seconds <= 0) {
                    if (nearest == null) {
                        kick(sender);
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "Teleporting...");
                        sender.teleport(nearest);
                    }

                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
                    sender.sendMessage(ChatColor.RED + "You moved in the 1 minute grace period. " + "The teleportation request has been cancelled.");
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (warn.contains(seconds)) {
                    sender.sendMessage(ChatColor.YELLOW + "You are in the process of being teleported, please wait!");
                }

                seconds--;
            }

        }.runTaskTimer(HCFactions.getInstance(), 0L, 20L);
    }

    private static String toStr(Location loc) {
        return "{x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + "}";
    }

    public static Location nearestSafeLocation(Location origin) {
        LandBoard landBoard = LandBoard.getInstance();

        if (landBoard.getClaim(origin) == null) {
            return (getActualHighestBlock(origin.getBlock()).getLocation().add(0, 1, 0));
        }

        // Start iterating outward on both positive and negative X & Z.
        for (int xPos = 2, xNeg = -2; xPos < 250; xPos += 2, xNeg -= 2) {
            for (int zPos = 2, zNeg = -2; zPos < 250; zPos += 2, zNeg -= 2) {
                Location atPos = origin.clone().add(xPos, 0, zPos);

                // Try to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atPos) == null && !isAdjacentClaimed(atPos)) {
                    return (getActualHighestBlock(atPos.getBlock()).getLocation().add(0, 1, 0));
                }

                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                // Try again to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atNeg) == null && !isAdjacentClaimed(atNeg)) {
                    return (getActualHighestBlock(atNeg.getBlock()).getLocation().add(0, 1, 0));
                }
            }
        }

        return (null);
    }

    private static Block getActualHighestBlock(Block block) {
        block = block.getWorld().getHighestBlockAt(block.getLocation());

        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return (block);
    }

    private static void kick(Player player) {
        player.setMetadata("loggedout", new FixedMetadataValue(HCFactions.getInstance(), true));
        player.kickPlayer(ChatColor.RED + "We couldn't find a safe location, so we safely logged you out for now. Contact a staff member before logging back on! " + ChatColor.BLUE + "TeamSpeak: ts." + HCFactions.getInstance().getServerHandler().getNetworkWebsite());
    }

    /**
     * @param base center block
     * @return list of all adjacent locations
     */
    private static List<Location> getAdjacent(Location base) {
        List<Location> adjacent = new ArrayList<>();

        // Add all relevant locations surrounding the base block
        for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                adjacent.add(base.getBlock().getRelative(face).getLocation());
            }
        }

        return adjacent;
    }

    /**
     * @param location location to check for
     * @return if any of it's blockfaces are claimed
     */
    private static boolean isAdjacentClaimed(Location location) {
        for (Location adjacent : getAdjacent(location)) {
            if (LandBoard.getInstance().getClaim(adjacent) != null) {
                return true; // we found a claim on an adjacent block!
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (warping.containsKey(player.getName())) {
                damaged.add(player.getName());
            }
        }
    }
}