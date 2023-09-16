package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CmdSetHQ {

    @Command(names={ "f sethome", "faction sethome", "fac sethome", "sethome" }, permission="")
    public static void teamSetHQ(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != faction) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set Home in your faction's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting Home outside of your faction's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }


            faction.setHQ(sender.getLocation());
            faction.sendMessage(ChatColor.GREEN + sender.getName() + " has updated the faction's Home!");
        } else {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
        }
    }

}