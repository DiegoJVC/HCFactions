package com.cobelpvp.hcfactions.events.systemfactions.koth.schedule;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class KOTHRegenerateSchedule {

    @Command(names = {"KOTHSchedule Regenerate", "KOTHSchedule Regen"}, permission = "hcfactions.koth.admin", async = true)
    public static void kothScheduleEnable(CommandSender sender) {
        File kothSchedule = new File(HCFactions.getInstance().getDataFolder(), "eventSchedule.json");

        if (kothSchedule.delete()) {
            HCFactions.getInstance().getEventHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The event schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete event schedule file.");
        }
    }

    @Command(names = {"KOTHSchedule debug"}, permission = "op")
    public static void kothScheduleDebug(CommandSender sender) {
        HCFactions.getInstance().getEventHandler().fillSchedule();
        sender.sendMessage(ChatColor.GREEN + "The event schedule has been filled.");
    }
}
