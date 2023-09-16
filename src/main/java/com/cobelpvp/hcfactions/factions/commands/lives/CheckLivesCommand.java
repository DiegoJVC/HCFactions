package com.cobelpvp.hcfactions.factions.commands.lives;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CheckLivesCommand {

    @Command(names = {"lives check"}, permission = "hcfactions.lives.check")
    public static void livesCheckCommand(CommandSender sender, @Param(name = "target") UUID target) {
        sender.sendMessage(ChatColor.GREEN + TeamsUUIDCache.name(target) + " have " + HCFactions.getInstance().getLivesMap().getLives(target) + " lives");
    }
}
