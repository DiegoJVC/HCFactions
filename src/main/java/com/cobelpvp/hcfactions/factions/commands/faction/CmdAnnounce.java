package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdAnnounce {

    @Command(names={"f announcement", "faction announcement", "fac announcement", "f anouncement", "faction anouncement", "fac anouncement" }, permission="")
    public static void teamAnnouncement(Player sender, @Param(name="new announcement", wildcard=true) String newAnnouncement) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (!(faction.isOwner(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
            return;
        }

        if (newAnnouncement.equalsIgnoreCase("clear")) {
            faction.setAnnouncement(null);
            sender.sendMessage(ChatColor.YELLOW + "Faction announcement cleared.");
            return;
        }

        faction.setAnnouncement(newAnnouncement);
        faction.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW  + " changed the faction announcement to " + ChatColor.LIGHT_PURPLE + newAnnouncement);
    }

}