package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdInvites {

    @Command(names={ "f invites", "faction invites", "fac invites" }, permission="")
    public static void teamInvites(Player sender) {
        StringBuilder yourInvites = new StringBuilder();

        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            if (faction.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(faction.getName()).append(ChatColor.YELLOW).append(", ");
            }
        }

        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites.toString());

        Faction current = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();

            for (UUID invited : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(UUIDUtils.name(invited)).append(ChatColor.YELLOW).append(", ");
            }

            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }

            sender.sendMessage(ChatColor.YELLOW + "Invited to your Faction: " + invitedToYourTeam.toString());
        }
    }

}