package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.hcfactions.factions.HCFactionsConstants;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdFaction {

    @Command(names = {"f", "faction", "fac"})
    public static void teamInfoCommand(CommandSender player, @Param(name = "page", defaultValue = "1") int page) {
        showPage(player, page);
    }

    private static void showPage(CommandSender sender, int pageNumber) {
        if (pageNumber == 1) {
            sender.sendMessage(HCFactionsConstants.getCenter(ChatColor.AQUA + "Faction Info"));
            sender.sendMessage(ChatColor.DARK_GREEN + "/f create <teamName> - Create a new faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f accept <teamName> - Accept a pending invitation");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f lives add <amount> - Irreversibly add lives to your faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f leave - Leave your current faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f home - Teleport to your faction home");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f stuck - Teleport out of enemy territory");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f deposit <amount|all> - Deposit money into your faction balance");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " *" + ChatColor.DARK_PURPLE +  "Current Page: 1/4 - /f help <page>");
        } else if (pageNumber == 2) {
            sender.sendMessage(HCFactionsConstants.getCenter(ChatColor.AQUA + "Faction Info"));
            sender.sendMessage(ChatColor.DARK_GREEN + "/f who [player|teamName] - Display faction information");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f map - Show nearby claims (identified by pillars)");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f list - Show list of factions online (sorted by most online)");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " *" + ChatColor.DARK_PURPLE +  "Current Page: 2/4 - /f help <page>");
        } else if (pageNumber == 3) {
            sender.sendMessage(HCFactionsConstants.getCenter(ChatColor.AQUA + "Faction Info"));
            sender.sendMessage(ChatColor.DARK_GREEN + "/f invite <player> - Invite a player to your faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f uninvite <player> - Revoke an invitation");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f invites - List all open invitations");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f kick <player> - Kick a player from your faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f claim - Start a claim for your faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f subclaim - Show the subclaim help page");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f sethome - Set your faction's home at your current location");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f withdraw <amount> - Withdraw money from your faction's balance");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f announcement [message here] - Set your faction's announcement");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " *" + ChatColor.DARK_PURPLE +  "Current Page: 3/4 - /f help <page>");
        } else if (pageNumber == 4) {
            sender.sendMessage(HCFactionsConstants.getCenter(ChatColor.AQUA + "Faction Info"));
            sender.sendMessage(ChatColor.DARK_GREEN + "/f coleader <add|remove> <player> - Add or remove a co-leader");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f captain <add|remove> <player> - Add or remove a captain");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f revive <player> - Revive a teammate using faction lives");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f unclaim [all] - Unclaim land");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f rename <newName> - Rename your faction");
            sender.sendMessage(ChatColor.DARK_GREEN + "/f disband - Disband your faction");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + " *" + ChatColor.DARK_PURPLE +  "Current Page: 4/4 - /f help <page>");
        }
    }
}