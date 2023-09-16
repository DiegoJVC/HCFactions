package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.destroythecore.DTC;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.atheneum.command.Command;

import static org.bukkit.ChatColor.*;

public class KOTHListCommand {
    
    @Command(names = { "KOTH List"}, permission = "hcfactions.koth")
    public static void kothList(Player sender) {
        if (HCFactions.getInstance().getEventHandler().getEvents().isEmpty()) {
            sender.sendMessage(RED + "There aren't any events set.");
            return;
        }
        
        for (Event event : HCFactions.getInstance().getEventHandler().getEvents()) {
            if (event.getType() == EventType.KOTH) {
                KOTH koth = (KOTH) event;
                sender.sendMessage((koth.isHidden() ? BLUE + "[H] " : "") + (koth.isActive() ? GREEN : RED) + koth.getName() + WHITE + " - " + GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + DARK_GRAY + "/" + GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + WHITE + "- " + GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
            } else if (event.getType() == EventType.DTC) {
                DTC dtc = (DTC) event;
                sender.sendMessage((dtc.isHidden() ? BLUE + "[H] " : "") + (dtc.isActive() ? GREEN : RED) + dtc.getName() + WHITE + " - " + GRAY + "P: " + dtc.getCurrentPoints());
            }
        }
    }
    
}