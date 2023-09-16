package com.cobelpvp.hcfactions.factions.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class PlayerJoinTeamEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Faction faction;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
