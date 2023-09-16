package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdSaveString {

    @Command(names={ "f savestring", "faction savestring", "fac savestring" }, permission="op")
    public static void teamSaveString(CommandSender sender, @Param(name="team", defaultValue="self") Faction faction) {
        String saveString = faction.saveString(false);

        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.UNDERLINE + "Save String (" + faction.getName() + ")");
        sender.sendMessage("");

        for (String line : saveString.split("\n")) {
            sender.sendMessage(ChatColor.BLUE + line.substring(0, line.indexOf(":")) + ": " + ChatColor.YELLOW + line.substring(line.indexOf(":") + 1).replace(",", ChatColor.BLUE + "," + ChatColor.YELLOW).replace(":", ChatColor.BLUE + ":" + ChatColor.YELLOW));
        }
    }

}