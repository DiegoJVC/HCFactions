package com.cobelpvp.hcfactions.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Claim;
import com.cobelpvp.hcfactions.factions.claims.Coordinate;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PacketBorderThread extends Thread {

    public static final int REGION_DISTANCE = 8;
    public static final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    private static Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<>();

    public PacketBorderThread() {
        super("HCFactions - Packet Border Thread");
    }

    public void run() {
        while (true) {
            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                try {
                    checkPlayer(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPlayer(Player player) {
        try {
            List<Claim> claims = new LinkedList<>();

            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            
            boolean tagged = SpawnTagHandler.isTagged(player);
            boolean hasPvPTimer = HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
            
            if (!tagged && !hasPvPTimer) {
                clearPlayer(player);
                return;
            }

            for (Map.Entry<Claim, Faction> regionDataEntry : LandBoard.getInstance().getRegionData(player.getLocation(), REGION_DISTANCE, REGION_DISTANCE, REGION_DISTANCE)) {
                Claim claim = regionDataEntry.getKey();
                Faction faction = regionDataEntry.getValue();

                if (claim.contains(player)) {
                    continue;
                }

                if (faction.getOwner() == null) {
                    if (faction.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE) && tagged) {
                        claims.add(claim);
                    } else if ((faction.hasDTRHCFClaim(DTRHCFClaim.KOTH) || faction.hasDTRHCFClaim(DTRHCFClaim.CITADEL)) && hasPvPTimer) {
                        claims.add(claim);
                    }
                } else {
                    if (HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        claims.add(claim);
                    }
                }
            }

            if (claims.size() == 0) {
                clearPlayer(player);
            } else {
                if (!sentBlockChanges.containsKey(player.getName())) {
                    sentBlockChanges.put(player.getName(), new HashMap<>());
                }

                Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

                while (bordersIterator.hasNext()) {
                    Map.Entry<Location, Long> border = bordersIterator.next();

                    if (System.currentTimeMillis() >= border.getValue()) {
                        Location loc = border.getKey();

                        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                            continue;
                        }

                        Block block = loc.getBlock();
                        player.sendBlockChange(loc, block.getType(), block.getData());
                        bordersIterator.remove();
                    }
                }

                for (Claim claim : claims) {
                    sendClaimToPlayer(player, claim);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendClaimToPlayer(Player player, Claim claim) {
        for (Coordinate coordinate : claim) {
            Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

            if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);

                if (check.getWorld().isChunkLoaded(check.getBlockX() >> 4, check.getBlockZ() >> 4) && check.getBlock().getType().isTransparent() && check.distanceSquared(onPlayerY) < REGION_DISTANCE_SQUARED) {
                    player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 10);
                    sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 4000L);
                }
            }
        }
    }

    private static void clearPlayer(Player player) {
        if (sentBlockChanges.containsKey(player.getName())) {
            for (Location changedLoc : sentBlockChanges.get(player.getName()).keySet()) {
                if (!changedLoc.getWorld().isChunkLoaded(changedLoc.getBlockX() >> 4, changedLoc.getBlockZ() >> 4)) {
                    continue;
                }

                Block block = changedLoc.getBlock();
                player.sendBlockChange(changedLoc, block.getType(), block.getData());
            }

            sentBlockChanges.remove(player.getName());
        }
    }

}