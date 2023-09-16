package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.command.CommandSender;

public class CmdJson {

    @Command(names={ "f json", "faction json", "fac json" }, permission="op")
    public static void teamJSON(CommandSender sender, @Param(name="team", defaultValue="self") Faction faction) {
        sender.sendMessage(faction.toJSON().toString());
    }

}