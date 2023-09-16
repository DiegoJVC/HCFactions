package com.cobelpvp.hcfactions.factions.commands.faction.subclaim;

import com.cobelpvp.hcfactions.factions.claims.VisualClaim;
import com.cobelpvp.hcfactions.factions.claims.VisualClaimType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.entity.Player;

public class TeamSubclaimMapCommand {

    @Command(names = {"f subclaim map", "faction subclaim map", "fac subclaim map", "f sub map", "faction sub map", "fac sub map"}, permission = "")
    public static void teamSubclaimMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, false)).draw(false);
    }

}