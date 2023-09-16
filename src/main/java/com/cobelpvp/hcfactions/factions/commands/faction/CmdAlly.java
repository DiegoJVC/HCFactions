package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdAlly {

    @Command(names={ "f ally", "faction ally", "fac ally" }, permission="")
    public static void teamAlly(Player sender, @Param(name="team") Faction faction) {
        Faction senderFaction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (senderFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (!(senderFaction.isOwner(sender.getUniqueId()) || senderFaction.isCaptain(sender.getUniqueId()) || senderFaction.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
            return;
        }

        if (senderFaction.equals(faction)) {
            sender.sendMessage(ChatColor.YELLOW + "You cannot ally your own faction!");
            return;
        }

        if (senderFaction.getAllies().size() >= HCFactions.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "Your faction already has the max number of allies, which is " + HCFactions.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }

        if (faction.getAllies().size() >= HCFactions.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "The faction you're trying to ally already has the max number of allies, which is " + HCFactions.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }

        if (senderFaction.isAlly(faction)) {
            sender.sendMessage(ChatColor.YELLOW + "You're already allied to " + faction.getName(sender) + ChatColor.YELLOW + ".");
            return;
        }

        TeamsNametagHandler.reloadPlayer(sender);

        if (senderFaction.getRequestedAllies().contains(faction.getUniqueId())) {
            senderFaction.getRequestedAllies().remove(faction.getUniqueId());

            faction.getAllies().add(senderFaction.getUniqueId());
            senderFaction.getAllies().add(faction.getUniqueId());

            faction.flagForSave();
            senderFaction.flagForSave();

            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (faction.isMember(player.getUniqueId())) {
                    player.sendMessage(senderFaction.getName(player) + ChatColor.YELLOW + " has accepted your request to ally. You now have " + Faction.ALLY_COLOR + faction.getAllies().size() + "/" + HCFactions.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                } else if (senderFaction.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your faction has allied " + faction.getName(sender) + ChatColor.YELLOW + ". You now have " + Faction.ALLY_COLOR + senderFaction.getAllies().size() + "/" + HCFactions.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                }

                if (faction.isMember(player.getUniqueId()) || senderFaction.isMember(player.getUniqueId())) {
                    TeamsNametagHandler.reloadPlayer(sender);
                    TeamsNametagHandler.reloadOthersFor(sender);
                }
            }
        } else {
            if (faction.getRequestedAllies().contains(senderFaction.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + "You have already requested to ally " + faction.getName(sender) + ChatColor.YELLOW + ".");
                return;
            }

            faction.getRequestedAllies().add(senderFaction.getUniqueId());
            faction.flagForSave();

            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (faction.isMember(player.getUniqueId())) {
                    player.sendMessage(senderFaction.getName(player.getPlayer()) + ChatColor.YELLOW + " has requested to be your ally. Type " + Faction.ALLY_COLOR + "/faction ally " + senderFaction.getName() + ChatColor.YELLOW + " to accept.");
                } else if (senderFaction.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your faction has requested to ally " + faction.getName(player) + ChatColor.YELLOW + ".");
                }
            }
        }
    }

}