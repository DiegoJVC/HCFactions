package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdUnally {

    @Command(names={ "f unally", "faction unally", "fac unally" }, permission="")
    public static void teamUnally(Player sender, @Param(name="team") Faction faction) {
        Faction senderFaction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (senderFaction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (!(senderFaction.isOwner(sender.getUniqueId()) || senderFaction.isCoLeader(sender.getUniqueId()) || senderFaction.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
            return;
        }

        if (!senderFaction.isAlly(faction)) {
            sender.sendMessage(ChatColor.RED + "You are not allied to " + faction.getName() + "!");
            return;
        }

        senderFaction.getAllies().remove(faction.getUniqueId());
        faction.getAllies().remove(senderFaction.getUniqueId());

        senderFaction.flagForSave();
        faction.flagForSave();

        for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
            if (faction.isMember(player.getUniqueId())) {
                player.sendMessage(senderFaction.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your faction.");
            } else if (senderFaction.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "Your faction has dropped its alliance with " + faction.getName(sender) + ChatColor.YELLOW + ".");
            }

            if (faction.isMember(player.getUniqueId()) || senderFaction.isMember(player.getUniqueId())) {
                TeamsNametagHandler.reloadPlayer(sender);
                TeamsNametagHandler.reloadOthersFor(sender);
            }
        }
    }

}