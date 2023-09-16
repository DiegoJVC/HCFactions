package com.cobelpvp.hcfactions.factions.commands.lives;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetLivesCommand {

    @Command(names = {"lives set"}, permission = "hcfactions.lives.set")
    public static void pvpSetLives(CommandSender sender, @Param(name = "player") UUID player, @Param(name = "amount") int amount) {
        HCFactions.getInstance().getLivesMap().setLives(player, amount);
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s life count to " + amount + ".");
    }

}