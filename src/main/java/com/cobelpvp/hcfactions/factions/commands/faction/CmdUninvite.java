package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.google.common.collect.ImmutableMap;

import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CmdUninvite {

    @Command(names={"f uninvite", "faction uninvite", "fac uninvite", "f revoke", "faction revoke", "fac revoke" }, permission = "")
    public static void teamUninvite(final Player sender, @Param(name="all | player") final String allPlayer) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return;
        }

        if (faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId())) {
            if (allPlayer.equalsIgnoreCase("all")) {
                faction.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                new BukkitRunnable() {

                    public void run() {
                        final UUID nameUUID = UUIDUtils.uuid(allPlayer);

                        new BukkitRunnable() {

                            public void run() {
                                if (faction.getInvitations().remove(nameUUID)) {
                                    TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_INVITE_REVOKED, ImmutableMap.of(
                                            "playerId", allPlayer,
                                            "uninvitedById", sender.getUniqueId(),
                                            "uninvitedByName", sender.getName()
                                    ));

                                    faction.getInvitations().remove(nameUUID);
                                    faction.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + allPlayer + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + allPlayer + "'!");
                                }
                            }

                        }.runTask(HCFactions.getInstance());
                    }

                }.runTaskAsynchronously(HCFactions.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
        }
    }

}