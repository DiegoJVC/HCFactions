package com.cobelpvp.hcfactions.events.systemfactions.koth.schedule;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.DARK_GRAY;

public class KOTHReloadSchedule {

    @Command(names = {"KOTHSchedule Reload"}, permission = "hcfactions.koth.admin")
    public static void kothScheduleReload(Player sender) {
        HCFactions.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + ChatColor.GREEN + "Reloaded the KOTH schedule.");
    }

}
