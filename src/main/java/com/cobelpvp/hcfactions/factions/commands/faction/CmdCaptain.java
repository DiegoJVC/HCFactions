package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdCaptain {

    @Command(names={"f mod add", "fac mod add", "faction mod add", "f captain add", "fac captain add", "faction captain add"}, permission="")
    public static void captainAdd(Player sender, @Param(name = "player") UUID promote) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender.getUniqueId());

        if (faction == null ) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if(!faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only faction co-leaders can execute this command.");
            return;
        }

        if(!faction.isMember(promote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your faction.");
            return;
        }

        if(faction.isOwner(promote) || faction.isCaptain(promote) || faction.isCoLeader(promote)) {
            sender.sendMessage(ChatColor.RED + "This player is already a captain (or above) of your faction.");
            return;
        }

        faction.removeCoLeader(promote);
        faction.addCaptain(promote);
        faction.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(promote) + " has been promoted to Captain!");
    }

    @Command(names={ "f mod remove", "fac mod remove", "faction mod remove", "f captain remove", "fac captain remove", "faction captain remove" }, permission="")
    public static void captainRemove(Player sender, @Param(name = "player") UUID demote) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender.getUniqueId());
        if( faction == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a faction to execute this command.");
            return;
        }

        if(!faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only faction co-leaders can execute this command.");
            return;
        }

        if(!faction.isMember(demote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your faction.");
            return;
        }

        if(!faction.isCaptain(demote)) {
            sender.sendMessage(ChatColor.RED + "This player is not a captain of your faction.");
            return;
        }

        faction.removeCoLeader(demote);
        faction.removeCaptain(demote);
        faction.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(demote) + " has been demoted to a member!");
    }

}
