package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdForceLeave {

    @Command(names={"f forceleave", "faction forceleave", "fac forceleave"})
    public static void forceLeave(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (faction.isOwner(sender.getUniqueId()) && faction.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your faction!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == faction) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your faction while on faction territory.");
            return;
        }

        if (faction.removeMember(sender.getUniqueId())) {
            faction.disband();
            HCFactions.getInstance().getFactionHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.YELLOW + "You have been left the faction.");
        } else {
            HCFactions.getInstance().getFactionHandler().setTeam(sender.getUniqueId(), null);
            faction.flagForSave();

            if (SpawnTagHandler.isTagged(sender)) {
                faction.setDTR(faction.getDTR() - 1);
                faction.sendMessage(ChatColor.RED + sender.getName() + " forcibly left the faction. Your faction has lost 1 DTR.");

                sender.sendMessage(ChatColor.RED + "You have forcibly left your faction. Your faction lost 1 DTR.");
            } else {
                faction.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the faction.");

                sender.sendMessage(ChatColor.YELLOW + "Successfully left the faction!");
            }
        }

        TeamsNametagHandler.reloadPlayer(sender);
        TeamsNametagHandler.reloadOthersFor(sender);
    }
}
