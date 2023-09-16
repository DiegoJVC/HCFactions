package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.dtr.DTRHandler;
import com.cobelpvp.hcfactions.factions.event.FullTeamBypassEvent;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdAccept {

    @Command(names = {"f accept", "faction accept", "fac accept", "f a", "faction a", "fac a", "f join", "faction join", "fac join", "f j", "faction j", "fac j"}, permission = "")
    public static void teamAccept(Player sender, @Param(name = "team") Faction faction) {
        if (faction.getInvitations().contains(sender.getUniqueId())) {
            if (HCFactions.getInstance().getFactionHandler().getTeam(sender) != null) {
                sender.sendMessage(ChatColor.DARK_GREEN + "You must leave your current faction first.");
                return;
            }

            if (faction.getMembers().size() >= HCFactions.getInstance().getMapHandler().getTeamSize()) {
                FullTeamBypassEvent attemptEvent = new FullTeamBypassEvent(sender, faction);
                HCFactions.getInstance().getServer().getPluginManager().callEvent(attemptEvent);

                if (!attemptEvent.isAllowBypass()) {
                    sender.sendMessage(ChatColor.RED + faction.getName() + " cannot be joined: Faction is full!");
                    return;
                }
            }

            if (DTRHandler.isOnCooldown(faction) && !HCFactions.getInstance().getServerHandler().isPreEOTW()) {
                sender.sendMessage(ChatColor.RED + faction.getName() + " cannot be joined: Faction not regenerating DTR!");
                return;
            }

            if (faction.getMembers().size() >= 15 && HCFactions.getInstance().getFactionHandler().isRostersLocked()) {
                sender.sendMessage(ChatColor.RED + faction.getName() + " cannot be joined: Faction rosters are locked server-wide!");
                return;
            }

            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + faction.getName() + " cannot be joined: You are combat tagged!");
                return;
            }

            faction.getInvitations().remove(sender.getUniqueId());
            faction.addMember(sender.getUniqueId());
            HCFactions.getInstance().getFactionHandler().setTeam(sender.getUniqueId(), faction);

            faction.sendMessage(ChatColor.RED + sender.getName() + " joined your faction.");

            TeamsNametagHandler.reloadPlayer(sender);
            TeamsNametagHandler.reloadOthersFor(sender);
            sender.sendMessage(ChatColor.DARK_GREEN + "You successfully joined " + ChatColor.RED + faction.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "This Faction has not invited you!");
        }
    }

}