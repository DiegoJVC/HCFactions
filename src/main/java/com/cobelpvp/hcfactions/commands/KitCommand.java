package com.cobelpvp.hcfactions.commands;

import org.bukkit.entity.Player;
import com.cobelpvp.atheneum.command.Command;

public class KitCommand {

    @Command(names = "kit", permission = "")
    public static void kit(Player sender) {
        sender.sendMessage("§6Enchant Limits: §aProtection 2, Sharpness 2, Power 3.");
    }
}
