package com.cobelpvp.hcfactions.factions.commands.lives;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class LivesCommand {

    @Command(names = {"lives"})
    public static void pvpLives(CommandSender sender, @Param(name = "player", defaultValue = "self") UUID player) {
        String name = UUIDUtils.name(player);

        sender.sendMessage(ChatColor.GOLD + name + "'s Lives: " + ChatColor.WHITE + HCFactions.getInstance().getLivesMap().getLives(player));
        sender.sendMessage(ChatColor.GREEN + "You can revive other players using /lives add <name> <amount>");
    }

}