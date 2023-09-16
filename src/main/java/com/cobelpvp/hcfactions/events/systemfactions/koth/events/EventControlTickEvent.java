package com.cobelpvp.hcfactions.events.systemfactions.koth.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EventControlTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH KOTH;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}