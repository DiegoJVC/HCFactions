package com.cobelpvp.hcfactions.events.systemfactions.koth.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.koth.EventScheduledTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import static org.bukkit.ChatColor.*;

public class KOTHScheduleCommand {

    public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE h:mm a");

    @Command(names = {"koth Schedule"}, permission = "")
    public static void kothSchedule(Player sender) {
        int sent = 0;
        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : HCFactions.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            Event resolved = HCFactions.getInstance().getEventHandler().getEvent(entry.getValue());

            if (resolved == null || resolved.isHidden() || !entry.getKey().toDate().after(now) || resolved.getType() != EventType.KOTH) {
                continue;
            }

            if (sent > 5) {
                break;
            }

            sent++;
            sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + ChatColor.GOLD + entry.getValue() + ChatColor.GREEN + " can be captured at " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GREEN + ".");
        }

        if (sent == 0) {
            sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.DARK_RED + "Undefined");
        } else {
            sender.sendMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
        }
    }

}
