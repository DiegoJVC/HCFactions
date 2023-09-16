package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class ToggleDatabaseTeamLog {

    @Command(names = {"toggledatabaseteamlog" }, permission = "op")
    public static void toggleDatabaseTeamLog(Player sender) {
        TeamActionTracker.setDatabaseLogEnabled(!TeamActionTracker.isDatabaseLogEnabled());
        sender.sendMessage("Enabled: " + TeamActionTracker.isDatabaseLogEnabled());
    }

}