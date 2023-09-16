package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.eotw.EOTW;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.cobelpvp.atheneum.command.Type;
import com.cobelpvp.atheneum.command.parameter.filter.NormalFilter;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.util.ColorText;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.regex.Pattern;

public class CmdCreate {

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Set<String> disallowedTeamNames = ImmutableSet.of("list", "Glowstone");

    @Command(names = {"f create", "faction create", "fac create"}, permission = "")
    public static void teamCreate(Player sender, @Param(name = "team") @Type(NormalFilter.class ) String team) {
        if (HCFactions.getInstance().getFactionHandler().getTeam(sender) != null) {
            sender.sendMessage(ChatColor.RED + "You already joined to faction.");
            return;
        }

        if (team.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum faction name size is 16 characters!");
            return;
        }

        if (team.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum faction name size is 3 characters!");
            return;
        }

        if (HCFactions.getInstance().getFactionHandler().getTeam(team) != null) {
            sender.sendMessage(ChatColor.RED + "That faction already exists!");
            return;
        }

        if (ALPHA_NUMERIC.matcher(team).find()) {
            sender.sendMessage(ChatColor.RED + "Faction names must be alphanumeric!");
            return;
        }

        if (EOTW.realFFAStarted()) {
            sender.sendMessage(ChatColor.RED + "You can't create factions during FFA.");
            return;
        }

        Faction createdFaction = new Faction(team);

        TeamsNametagHandler.reloadPlayer(sender);

        TeamActionTracker.logActionAsync(createdFaction, TeamActionType.PLAYER_CREATE_TEAM, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName()
        ));

        createdFaction.setUniqueId(new ObjectId());
        createdFaction.setOwner(sender.getUniqueId());
        createdFaction.setName(team);
        createdFaction.setDTR(1);

        HCFactions.getInstance().getFactionHandler().setupTeam(createdFaction);

        String watcherNameStars = ChatColor.RED + "";
        if (createdFaction.isOwner(sender.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "**";
        } else if (createdFaction.isCoLeader(sender.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "**";
        } else if (createdFaction.isCaptain(sender.getUniqueId())) {
            watcherNameStars += ChatColor.RED + "*";
        }
        Bukkit.broadcastMessage(ColorText.translate(watcherNameStars + createdFaction.getName() + " " + sender.getName() + " &ecreated a new faction &c" + createdFaction.getName()));
    }

}