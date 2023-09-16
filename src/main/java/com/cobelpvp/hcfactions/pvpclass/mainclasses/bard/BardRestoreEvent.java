package com.cobelpvp.hcfactions.pvpclass.mainclasses.bard;

import com.cobelpvp.hcfactions.pvpclass.PvPClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class BardRestoreEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private Player player;
    @Getter private PvPClass.SavedPotion potions;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}