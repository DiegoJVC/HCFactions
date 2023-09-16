package com.cobelpvp.hcfactions.commands;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.common.io.Files;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class ReviveCommand {

    @Command(names={ "Revive" }, permission="hcfactions.revive")
    public static void revive(CommandSender sender, @Param(name="player") UUID player, @Param(name="reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.DARK_RED + ". is not a good reason.");
            return;
        }

        if (HCFactions.getInstance().getDeathbanMap().isDeathbanned(player)) {
            File logTo = new File(new File("hcfactionslogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + UUIDUtils.name(player) + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HCFactions.getInstance().getDeathbanMap().revive(player);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(player) + "!");
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "That player is not deathbanned!");
        }
    }

}
