package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdForceLeave {

    @Command(names={ "forceleave" }, permission="hcfactions.forceleave")
    public static void forceLeave(Player player) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (faction == null) {
            player.sendMessage(ChatColor.RED + "You are not on a faction!");
            return;
        }

        faction.removeMember(player.getUniqueId());
        HCFactions.getInstance().getFactionHandler().setTeam(player.getUniqueId(), null);
        player.sendMessage(ChatColor.RED + "Force left your faction.");
    }

}