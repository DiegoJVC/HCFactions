package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.factions.claims.VisualClaim;
import com.cobelpvp.hcfactions.factions.claims.VisualClaimType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.entity.Player;

public class CmdMap {

    @Command(names = {"f map", "faction map", "fac map", "map"}, permission = "")
    public static void teamMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.MAP, false)).draw(false);
    }
}