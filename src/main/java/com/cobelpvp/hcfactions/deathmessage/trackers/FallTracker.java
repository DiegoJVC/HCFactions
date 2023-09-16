package com.cobelpvp.hcfactions.deathmessage.trackers;

import com.cobelpvp.hcfactions.deathmessage.objects.Damage;
import com.cobelpvp.hcfactions.deathmessage.DeathMessageHandler;
import com.cobelpvp.hcfactions.deathmessage.event.CustomPlayerDamageEvent;
import com.cobelpvp.hcfactions.deathmessage.objects.PlayerDamage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FallTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;

        if (record != null) {
            for (Damage damage : record) {
                if (damage instanceof FallDamage || damage instanceof FallDamageByPlayer) {
                    continue;
                }

                if (damage instanceof PlayerDamage && (knocker == null || damage.getTime() > knockerTime)) {
                    knocker = damage;
                    knockerTime = damage.getTime();
                }
            }
        }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis() ) {
            event.setTrackerDamage(new FallDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage) knocker).getDamager()));
        } else {
            event.setTrackerDamage(new FallDamage(event.getPlayer().getName(), event.getDamage()));
        }
    }

    public static class FallDamage extends Damage {

        public FallDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " believed they could fly.");
        }

    }

    public static class FallDamageByPlayer extends PlayerDamage {

        public FallDamageByPlayer(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " fell to their death while fighting " + wrapName(getDamager()) + ".");
        }

    }

}