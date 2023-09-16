package com.cobelpvp.hcfactions.events.systemfactions.koth.schedule;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDisableSchedule {

    @Command(names = "KOTHSchedule Disable", permission = "hcfactions.koth.admin")
    public static void kothScheduleDisable(CommandSender sender) {
        HCFactions.getInstance().getEventHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.GOLD + "The KOTH schedule has been " + ChatColor.DARK_RED + "disabled" + ChatColor.GOLD + ".");
    }

}
