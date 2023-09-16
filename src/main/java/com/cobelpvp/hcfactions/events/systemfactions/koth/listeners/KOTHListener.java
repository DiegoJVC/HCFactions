package com.cobelpvp.hcfactions.events.systemfactions.koth.listeners;

import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.hcfactions.events.systemfactions.koth.events.EventControlTickEvent;

public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (event.getKOTH().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = (KOTH) event.getKOTH();
        if (koth.getRemainingCapTime() % 180 == 0 && koth.getRemainingCapTime() <= (koth.getCapTime() - 30)) {
            HCFactions.getInstance().getServer().broadcastMessage(ColorText.translate("&8[&3&lKOTH&8] &3" + koth.getName() + " &6is trying to be controlled."));
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.GREEN + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()));
        }
    }

}