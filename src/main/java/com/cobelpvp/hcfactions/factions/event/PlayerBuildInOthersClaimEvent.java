package com.cobelpvp.hcfactions.factions.event;

import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBuildInOthersClaimEvent extends PlayerEvent {

    @Getter
    private static HandlerList handlerList = new HandlerList();
    @Getter
    private final Block block;
    @Getter
    private final Faction faction;
    @Getter
    @Setter
    private boolean willIgnore;

    public PlayerBuildInOthersClaimEvent(Player who, Block block, Faction faction) {
        super(who);
        this.block = block;
        this.faction = faction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
