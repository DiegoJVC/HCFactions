package com.cobelpvp.hcfactions.factions.commands.lives;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddLivesCommand {

    @Command(names = "lives add", permission = "hcfactions.lives.add")
    public static void pvpGiveLives(Player sender, @Param(name = "player") UUID player, @Param(name = "amount") int amount) {
        if (HCFactions.getInstance().getLivesMap().getLives(sender.getUniqueId()) < amount) {
            sender.sendMessage("You don't have sufficient lives to give.");
            return;
        }

        HCFactions.getInstance().getLivesMap().setLives(sender.getUniqueId(), HCFactions.getInstance().getLivesMap().getLives(sender.getUniqueId()) - amount);
        HCFactions.getInstance().getLivesMap().setLives(player, amount);
        sender.sendMessage("You gave x" + amount + " lives to " + UUIDUtils.name(player));
        Bukkit.getPlayer(player).sendMessage("Received x" + amount + " lives for " + sender.getName() + ".");
    }
}
