package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.pvpclass.PvPClass;
import com.cobelpvp.hcfactions.pvpclass.PvPClassHandler;
import com.cobelpvp.hcfactions.pvpclass.mainclasses.MinerClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class BlockConvenienceListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExpBreak(BlockBreakEvent event) {
        event.getPlayer().giveExp(event.getExpToDrop());
        event.setExpToDrop(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null || event.getEntity() instanceof Player) {
            return;
        }

        killer.giveExp(event.getDroppedExp());
        event.setDroppedExp(0);
    }

    private boolean hasMinerClass(Player player) {
        PvPClass pvpClass = PvPClassHandler.getPvPClass(player);
        return pvpClass != null && pvpClass.getClass() == MinerClass.class;
    }
}
