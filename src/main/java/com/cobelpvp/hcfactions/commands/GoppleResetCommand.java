package com.cobelpvp.hcfactions.commands;

import java.util.UUID;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class GoppleResetCommand {

    @Command(names={ "gapple reset" }, permission="hcfactions.gopplereset")
    public static void goppleReset(Player sender, @Param(name="player") UUID player) {
        HCFactions.getInstance().getOppleMap().resetCooldown(player);
        sender.sendMessage(ChatColor.GREEN + "Cooldown reset!");
    }

}