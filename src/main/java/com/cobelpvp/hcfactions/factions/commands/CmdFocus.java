package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdFocus {

    @Command(names={ "focus" }, permission="")
    public static void focus(Player sender, @Param(name="player") Player target) {
        Faction senderFaction = HCFactions.getInstance().getFactionHandler().getTeam(sender);
        Faction targetFaction = HCFactions.getInstance().getFactionHandler().getTeam(target);

        if (senderFaction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (senderFaction == targetFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your faction.");
            return;
        }

        senderFaction.setFocused(target.getUniqueId());
        senderFaction.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : HCFactions.getInstance().getServer().getOnlinePlayers()) {
            if (senderFaction.isMember(onlinePlayer.getUniqueId())) {
                TeamsNametagHandler.reloadOthersFor(onlinePlayer);
            }
        }
    }

}