package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.entity.Player;

public class CmdHelp {

    @Command(names={"f help", "faction help", "fac help" })
    public static void teamHelp(Player player, @Param(name = "page", defaultValue = "1") int page) {
        CmdFaction.teamInfoCommand(player, page);
    }

}