package com.cobelpvp.hcfactions.factions.commands.pvp;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPTimeCommand {

    @Command(names = {"pvptimer time", "timer time", "pvp time"}, permission = "")
    public static void pvpTime(Player sender) {
        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.formatIntoMMSS(HCFactions.getInstance().getPvPTimerMap().getSecondsRemaining(sender.getUniqueId())) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}