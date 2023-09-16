package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import java.util.Date;
import java.util.Map;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.koth.EventScheduledTime;
import org.bukkit.entity.Player;
import mkremins.fanciful.FancyMessage;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.atheneum.command.Command;

import static org.bukkit.ChatColor.*;

public class KOTHCommand {

    @Command(names={ "koth", "koth next", "koth info" }, permission="")
    public static void koth(Player sender) {
        for (Event koth : HCFactions.getInstance().getEventHandler().getEvents()) {
            if (!koth.isHidden() && koth.isActive()) {
                FancyMessage fm = new FancyMessage("[Koth] ")
                        .color(BLUE)
                        .then(koth.getName())
                            .color(GREEN)
                            .style(UNDERLINE);
                            if (koth instanceof KOTH) {
                                fm.tooltip(YELLOW.toString() + ((KOTH) koth).getCapLocation().getBlockX() + ", " + ((KOTH) koth).getCapLocation().getBlockZ());
                            }
                            fm.color(GREEN)
                        .then(" can be contested now.")
                            .color(GREEN);
                        fm.send(sender);
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : HCFactions.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + GOLD + entry.getValue() + GREEN + " can be captured at " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GREEN + ".");
                sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + GREEN + "It is currently " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(now) + GREEN + ".");
                sender.sendMessage(GREEN + "Type '/koth schedule' to see the complete list of koth schedule.");
                return;
            }
        }

        sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "]"+ RED + "No info Available");
    }

}