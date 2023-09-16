package com.cobelpvp.hcfactions.factions.commands.faction.chatspy;

import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyCommand {

    @Command(names = {"f chatspy", "faction chatspy", "fac chatspy"}, permission = "hcfactions.chatspy")
    public static void teamChatSpy(Player sender) {
        sender.sendMessage(ChatColor.RED + "/faction chatspy list - views factions who you are spying on");
        sender.sendMessage(ChatColor.RED + "/faction chatspy add - starts spying on a faction");
        sender.sendMessage(ChatColor.RED + "/faction chatspy del - stops spying on a faction");
        sender.sendMessage(ChatColor.RED + "/faction chatspy clear - stops spying on all factions");
    }

}