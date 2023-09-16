package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.FactionHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdFreezeRosters {

    @Command(names={ "freezerosters" }, permission="hcfactions.freezerosters")
    public static void freezeRosters(Player sender) {
        FactionHandler factionHandler = HCFactions.getInstance().getFactionHandler();
        factionHandler.setRostersLocked(!factionHandler.isRostersLocked());

        sender.sendMessage(ChatColor.YELLOW + "Faction rosters are now " + ChatColor.RED + (factionHandler.isRostersLocked() ? "locked" : "unlocked") + ChatColor.YELLOW + ".");
    }

}