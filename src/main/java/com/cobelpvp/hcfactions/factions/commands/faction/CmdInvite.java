package com.cobelpvp.hcfactions.factions.commands.faction;

import com.google.common.collect.ImmutableMap;

import mkremins.fanciful.FancyMessage;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.event.FullTeamBypassEvent;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdInvite {

    @Command(names={ "f invite", "faction invite", "fac invite", "f inv", "faction inv", "fac inv" }, permission="")
    public static void teamInvite(Player sender, @Param(name="player") UUID player, @Param(name="override?", defaultValue="something-not-override") String override) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
            return;
        }

        if (faction.getMembers().size() >= HCFactions.getInstance().getMapHandler().getTeamSize()) {
            FullTeamBypassEvent bypassEvent = new FullTeamBypassEvent(sender, faction);
            HCFactions.getInstance().getServer().getPluginManager().callEvent(bypassEvent);

            if (!bypassEvent.isAllowBypass()) {
                sender.sendMessage(ChatColor.RED + "The max faction size is " + HCFactions.getInstance().getMapHandler().getTeamSize() + (bypassEvent.getExtraSlots() == 0 ? "" : " (+" + bypassEvent.getExtraSlots() + ")") + "!");
                return;
            }
        }

        if (!(faction.isOwner(sender.getUniqueId()) || faction.isCoLeader(sender.getUniqueId()) || faction.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only faction captains can do this.");
            return;
        }

        if (faction.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already on your faction.");
            return;
        }

        if (faction.getInvitations().contains(player)) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        TeamActionTracker.logActionAsync(faction, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
                "playerId", player,
                "invitedById", sender.getUniqueId(),
                "invitedByName", sender.getName(),
                "betrayOverride", override.equalsIgnoreCase("override")
        ));

        faction.getInvitations().add(player);
        faction.flagForSave();

        Player bukkitPlayer = HCFactions.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "You have been invited to join " + ChatColor.RED + faction.getName() + ".");
            FancyMessage inviteClick = new FancyMessage("To join ").color(ChatColor.YELLOW).then("click here ").tooltip("§eClick to join").command("/faction join " + faction.getName()).color(ChatColor.GREEN);
            inviteClick.then("or type ").color(ChatColor.YELLOW).then("/faction join " + faction.getName()).color(ChatColor.GREEN);

            inviteClick.send(bukkitPlayer);
        }

        String watcherNameStars = ChatColor.RED + "";
        if (faction.isOwner(sender.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "**";
        } else if (faction.isCoLeader(sender.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "**";
        } else if (faction.isCaptain(sender.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "*";
        }

        faction.sendMessage(ChatColor.DARK_GREEN + watcherNameStars + faction.getName() + " " + sender.getName() + ChatColor.YELLOW + " invited " + ChatColor.RED + UUIDUtils.name(player) + ChatColor.YELLOW + " to your faction.");
    }

}