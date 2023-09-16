package com.cobelpvp.hcfactions.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.server.event.EnderpearlCooldownAppliedEvent;
import lombok.Getter;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class EnderpearlCooldownHandler implements Listener {

	@Getter private static Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) event.getEntity().getShooter();

		if (event.getEntity() instanceof EnderPearl) {
			shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(HCFactions.getInstance(), event.getEntity()));

			long timeToApply = DTRHCFClaim.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(event.getEntity().getLocation()) ? 30_000L : 16_000L;

			EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, timeToApply);
			HCFactions.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

			enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl)) {
			return;
		}

		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player thrower = (Player) event.getEntity().getShooter();

		if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
			long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();

			double value = (millisLeft / 1000D);
			double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;

			event.setCancelled(true);
			thrower.sendMessage(ChatColor.DARK_AQUA + "Pearl cooldown: " + ChatColor.RED + sec + " seconds");
			thrower.updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		} else if (!enderpearlCooldown.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
			return;
		}

		Location target = event.getTo();
		Location from = event.getFrom();

		if (DTRHCFClaim.SAFE_ZONE.appliesAt(target)) {
			if (!DTRHCFClaim.SAFE_ZONE.appliesAt(from)) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into spawn!");
				return;
			}
		}

		if (DTRHCFClaim.NO_ENDERPEARL.appliesAt(target)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into this region!");
			return;
		}

		Faction ownerTo = LandBoard.getInstance().getTeam(event.getTo());

		if (HCFactions.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && ownerTo != null) {
			if (ownerTo.isMember(event.getPlayer().getUniqueId())) {
				HCFactions.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
			} else if (ownerTo.getOwner() != null || (DTRHCFClaim.KOTH.appliesAt(event.getTo()) || DTRHCFClaim.CITADEL.appliesAt(event.getTo()))) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into claims while having a PvP Timer!");
				return;
			}
		}
	}

	@EventHandler
	public void onRefund(PlayerPearlRefundEvent event) {
		Player player = event.getPlayer();

		if (!player.isOnline()) {
			return;
		}

		ItemStack inPlayerHand = player.getItemInHand();
		if (inPlayerHand != null && inPlayerHand.getType() == Material.ENDER_PEARL && inPlayerHand.getAmount() < 16) {
			inPlayerHand.setAmount(inPlayerHand.getAmount() + 1);
			player.updateInventory();
		}

		enderpearlCooldown.remove(player.getName());
	}

	public boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) ||
		        (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public static void resetEnderpearlTimer(Player player) {
		if (DTRHCFClaim.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(player.getLocation())) {
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 30_000L);
		} else {
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 16_000L);
		}
	}

}