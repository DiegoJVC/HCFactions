package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.ColorText;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdRename {

    @Command(names={ "f rename", "faction rename", "fac rename" }, permission="")
    public static void teamRename(Player sender, @Param(name="new name") String newName) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (HCFactions.getInstance().getCitadelHandler().getCappers().contains(faction.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Citadel cappers cannot change their name. Please contact an admin to rename your faction.");
            return;
        }

        if (!faction.isOwner(sender.getUniqueId()) && !faction.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only faction owners and co-leaders can use this command!");
            return;
        }

        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum faction name size is 16 characters!");
            return;
        }

        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum faction name size is 3 characters!");
            return;
        }

        if (!CmdCreate.ALPHA_NUMERIC.matcher(newName).find()) {
            if (HCFactions.getInstance().getFactionHandler().getTeam(newName) == null) {
                String lastName = faction.getName();
                faction.rename(newName);
                Bukkit.broadcastMessage(ColorText.translate("&eThe faction &c" + lastName + " &echanged their named to &c" + faction.getName() + "."));
            } else {
                sender.sendMessage(ChatColor.RED + "A faction with that name already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Faction names must be alphanumeric!");
        }
    }

}