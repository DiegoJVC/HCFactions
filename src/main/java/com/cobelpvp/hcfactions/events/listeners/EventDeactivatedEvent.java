package com.cobelpvp.hcfactions.events.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EventDeactivatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private com.cobelpvp.hcfactions.events.Event event;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}