package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdCoLeader {

    @Command(names={"f co-leader add", "fac co-leader add", "faction co-leader add", "f coleader add", "fac coleader add", "faction coleader add"}, permission="")
    public static void coleaderAdd(Player sender, @Param(name = "player") UUID promote) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender.getUniqueId());

        if (faction == null ) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if(!faction.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the faction owner can execute this command.");
            return;
        }

        if(!faction.isMember(promote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your faction.");
            return;
        }

        if(faction.isOwner(promote) || faction.isCoLeader(promote)) {
           sender.sendMessage(ChatColor.RED + "This player is already a co-leader (or above) of your faction.");
            return;
        }

        faction.addCoLeader(promote);
        faction.removeCaptain(promote);
        faction.sendMessage(org.bukkit.ChatColor.GOLD + UUIDUtils.name(promote) + " has been promoted to Co-Leader!");
    }

    @Command(names={"f co-leader remove", "fac co-leader remove", "faction co-leader remove", "f coleader remove", "fac coleader remove", "faction coleader remove" }, permission="")
    public static void coleaderRemove(Player sender, @Param(name = "player") UUID demote) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender.getUniqueId());

        if (faction == null ) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if(!faction.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the faction owner can execute this command.");
            return;
        }

        if(!faction.isMember(demote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your faction.");
            return;
        }

        if(!faction.isCoLeader(demote)) {
            sender.sendMessage(ChatColor.RED + "This player is not a co-leader of your faction.");
            return;
        }

        faction.removeCoLeader(demote);
        faction.removeCaptain(demote);
        faction.sendMessage(org.bukkit.ChatColor.YELLOW + UUIDUtils.name(demote) + " has been demoted to a member!");
    }
}
