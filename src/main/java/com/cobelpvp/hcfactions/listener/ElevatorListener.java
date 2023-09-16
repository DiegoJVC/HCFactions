package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {

    @EventHandler
    public void onSignUpdate(SignChangeEvent event) {
        if (StringUtils.containsIgnoreCase(event.getLine(0), "Elevator")) {
            boolean up;
            if (StringUtils.containsIgnoreCase(event.getLine(1), "Up")) {
                up = true;
            } else {
                if (!StringUtils.containsIgnoreCase(event.getLine(1), "Down")) {
                    event.getPlayer().sendMessage(ChatColor.RED + "&cError: &4Up or Down");
                }
                up = false;
            }

            event.setLine(0, ChatColor.RED + "[Elevator]");
            event.setLine(1, up ? "Up" : "Down");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                String[] lines = sign.getLines();

                if (lines[0].equals(ChatColor.RED + "[Elevator]")) {
                    boolean up;
                    if (lines[1].equals("Up")) {
                        up = true;
                    } else {
                        if (!lines[1].equalsIgnoreCase("Down")) return;
                        up = false;
                    }

                    elevatorClick(event.getPlayer(), sign.getLocation(), up);
                }
            }
        }
    }

    public boolean elevatorClick(Player player, Location location, boolean up) {
        Block block = location.getBlock();

        do {
            block = block.getRelative(up ? BlockFace.UP : BlockFace.DOWN);

            if (block.getY() > block.getWorld().getMaxHeight() || block.getY() <= 1) {
                player.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Could not find a sign " + (up ? "above" : "below") + " to teleport you to.");
                return false;
            }
        } while (!isSign(block));

        boolean underSafe = isSafe(block.getRelative(BlockFace.DOWN));
        boolean overSafe = isSafe(block.getRelative(BlockFace.UP));

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            Location plocation = player.getLocation().clone();
            plocation.setX(block.getX() + 0.5);
            plocation.setY((double) (block.getY() + (underSafe ? -1 : 0)));
            plocation.setZ(block.getZ() + 0.5);
            player.teleport(plocation);
            return true;
        }

        Faction faction = LandBoard.getInstance().getTeam(location);
        if (!faction.isMember(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have permissions to use elevators here.");
            return false;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You cannot use elevators with PvP Tag.");
            return false;
        }

        if (!underSafe && !overSafe) {
            player.sendMessage(ChatColor.RED + "There is a block blocking the sign " + (up ? "above" : "below") + "!");
            return false;
        }

        Location wlocation = player.getLocation().clone();
        wlocation.setX(block.getX() + 0.5);
        wlocation.setY((double) (block.getY() + (underSafe ? -1 : 0)));
        wlocation.setZ(block.getZ() + 0.5);
        wlocation.setPitch(0.0F);
        player.teleport(wlocation);
        return true;
    }

    private boolean isSign(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            return lines[0].equals(ChatColor.RED + "[Elevator]") && (lines[1].equalsIgnoreCase("Up") || lines[1].equalsIgnoreCase("Down"));
        }

        return false;
    }

    private boolean isSafe(Block block) {
        return block != null && !block.getType().isSolid() && block.getType() != Material.GLASS && block.getType() != Material.STAINED_GLASS;
    }
}
