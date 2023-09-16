package com.cobelpvp.hcfactions.events.systemfactions.koth.schedule;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHEnableSchedule {

    @Command(names = "KOTHSchedule Enable", permission = "hcfactions.koth.admin")
    public static void kothScheduleEnable(CommandSender sender) {
        HCFactions.getInstance().getEventHandler().setScheduleEnabled(true);

        sender.sendMessage(ChatColor.GOLD + "The KOTH schedule has been " + ChatColor.GREEN + "enabled" + ChatColor.GOLD + ".");
    }

}
