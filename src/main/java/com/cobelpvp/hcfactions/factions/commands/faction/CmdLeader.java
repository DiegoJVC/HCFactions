package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class CmdLeader {

    @Command(names={ "f newleader", "faction newleader", "fac newleader", "f leader", "faction leader", "fac leader" }, permission="")
    public static void teamLeader(Player sender, @Param(name="player") UUID player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (!faction.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the faction leader can do this.");
            return;
        }

        if (!faction.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on your faction.");
            return;
        }

        faction.sendMessage(ChatColor.GOLD + UUIDUtils.name(player) + " has been given ownership of " + faction.getName() + ".");
        faction.setOwner(player);
        faction.addCaptain(sender.getUniqueId());
    }

}