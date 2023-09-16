package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdInfo {

    @Command(names={ "f info", "faction info", "fac info", "f who", "faction who", "fac who", "f show", "faction show", "fac show", "f i", "faction i", "fac i" }, permission="")
    public static void teamInfo(final Player sender, @Param(name="team", defaultValue="self", tabCompleteFlags={ "noteams", "players" }) final Faction faction) {
        new BukkitRunnable() {

            public void run() {
                Faction exactPlayerFaction = HCFactions.getInstance().getFactionHandler().getTeam(UUIDUtils.uuid(faction.getName()));

                if (exactPlayerFaction != null && exactPlayerFaction != faction) {
                    exactPlayerFaction.sendTeamInfo(sender);
                }

                faction.sendTeamInfo(sender);
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

}