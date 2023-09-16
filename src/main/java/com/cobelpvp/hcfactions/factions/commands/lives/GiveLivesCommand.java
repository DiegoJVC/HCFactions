package com.cobelpvp.hcfactions.factions.commands.lives;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GiveLivesCommand {

    @Command(names = {"lives give"}, permission = "hcfactions.lives.give")
    public static void pvpSetLives(CommandSender sender, @Param(name = "player") UUID player, @Param(name = "amount") int amount) {
        HCFactions.getInstance().getLivesMap().setLives(player, HCFactions.getInstance().getLivesMap().getLives(player) + amount);
        sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " lives.");

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            String suffix = sender instanceof Player ? " from " + sender.getName() : "";
            bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
        }
    }

}
