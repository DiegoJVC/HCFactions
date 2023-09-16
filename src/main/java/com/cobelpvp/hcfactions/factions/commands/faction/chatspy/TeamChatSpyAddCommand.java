package com.cobelpvp.hcfactions.factions.commands.faction.chatspy;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamChatSpyAddCommand {

    @Command(names = {"f chatspy add", "faction chatspy add", "fac chatspy add"}, permission = "hcfactions.chatspy")
    public static void teamChatSpyAdd(Player sender, @Param(name = "team") Faction faction) {
        if (HCFactions.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(faction.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already spying on " + faction.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(HCFactions.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.add(faction.getUniqueId());

        HCFactions.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are now spying on the chat of " + ChatColor.YELLOW + faction.getName() + ChatColor.GREEN + ".");
    }

}