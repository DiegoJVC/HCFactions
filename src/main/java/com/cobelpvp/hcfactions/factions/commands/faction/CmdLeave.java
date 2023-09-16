package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CmdLeave {

    @Command(names={ "f leave", "faction leave", "fac leave" }, permission="")
    public static void teamLeave(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not on a faction!");
            return;
        }

        if (faction.isOwner(sender.getUniqueId()) && faction.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your faction!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == faction) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your faction while on team territory.");
            return;
        }

        if(SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(ChatColor.RED + "You are combat-tagged! You can only leave your faction by using '" + ChatColor.YELLOW + "/f forceleave" + ChatColor.RED + "' which will cost your faction 1 DTR.");
            return;
        }

        if (faction.removeMember(sender.getUniqueId())) {
            faction.disband();
            HCFactions.getInstance().getFactionHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_RED + "Successfully left and disbanded faction!");
        } else {
            HCFactions.getInstance().getFactionHandler().setTeam(sender.getUniqueId(), null);
            faction.flagForSave();
            faction.sendMessage(ChatColor.RED + sender.getName() + " has left the faction.");

            sender.sendMessage(ChatColor.BLUE + "Successfully left the faction!");
        }

        TeamsNametagHandler.reloadPlayer(sender);
        TeamsNametagHandler.reloadOthersFor(sender);
    }

}