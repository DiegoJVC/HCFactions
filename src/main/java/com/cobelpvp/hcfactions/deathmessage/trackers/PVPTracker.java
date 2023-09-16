package com.cobelpvp.hcfactions.deathmessage.trackers;

import com.cobelpvp.hcfactions.HCFactions;
import com.google.common.collect.Lists;
import com.cobelpvp.hcfactions.deathmessage.event.CustomPlayerDamageEvent;
import com.cobelpvp.hcfactions.deathmessage.util.MobUtil;
import com.cobelpvp.hcfactions.deathmessage.objects.PlayerDamage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class PVPTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player damaged = event.getPlayer();

                event.setTrackerDamage(new PVPDamage(damaged.getName(), event.getDamage(), damager.getName(), damager.getItemInHand()));
            }
        }
    }

    public static class PVPDamage extends PlayerDamage {

        private String itemString;

        public PVPDamage(String damaged, double damage, String damager, ItemStack itemStack) {
            super(damaged, damage, damager);
            this.itemString = "Error";

            if (itemStack.getType() == Material.AIR) {
                itemString = "their fists";
            } else {
                itemString = MobUtil.getItemName(itemStack);
            }
        }

        public String getDeathMessage() {
            List<String> names = Lists.newArrayList();

            names.add(" killed ");
            names.add(" killed ");
            Random random = new Random();
            String randomMessage = names.get(random.nextInt(names.size()));

            String extension = (HCFactions.getInstance().getInDuelPredicate().test(Bukkit.getPlayer(getDamaged()))) ?
                    " during a duel."
                    :
                    " using an " + ChatColor.RED + itemString;
            return (wrapName(getDamager()) + randomMessage + wrapName(getDamaged()) + extension);
        }

    }

}