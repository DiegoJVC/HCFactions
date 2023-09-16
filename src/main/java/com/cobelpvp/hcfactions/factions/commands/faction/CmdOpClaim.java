package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.VisualClaim;
import com.cobelpvp.hcfactions.factions.claims.VisualClaimType;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdOpClaim {

    @Command(names={ "f systemclaim", "faction systemclaim", "fac systemclaim" }, permission = "worldedit.*")
    public static void teamOpClaim(final Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.DARK_RED + "You must be in a faction.");
            return;
        }

        sender.getInventory().remove(CmdClaim.SELECTION_WAND);

        new BukkitRunnable() {

            public void run() {
                sender.getInventory().addItem(CmdClaim.SELECTION_WAND.clone());
            }

        }.runTaskLater(HCFactions.getInstance(), 1L);

        new VisualClaim(sender, VisualClaimType.CREATE, true).draw(false);

        if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.MAP, true).draw(true);
        }
    }

}