package com.cobelpvp.hcfactions.deathmessage.event;

import com.cobelpvp.hcfactions.deathmessage.objects.Damage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

@AllArgsConstructor
public class CustomPlayerDamageEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    @Getter private EntityDamageEvent cause;
    @Getter @Setter private Damage trackerDamage;

    public Player getPlayer() {
        return ((Player) cause.getEntity());
    }

    public double getDamage() {
        return (cause.getDamage());
    }

    public HandlerList getHandlers() {
        return (handlerList);
    }

    public static HandlerList getHandlerList() {
        return (handlerList);
    }

}