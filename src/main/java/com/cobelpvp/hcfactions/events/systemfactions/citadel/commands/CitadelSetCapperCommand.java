package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CitadelSetCapperCommand {

    @Command(names={ "citadel setcapper" }, permission="hcfactions.citadel.admin")
    public static void citadelSetCapper(Player sender, @Param(name="cappers") String cappers) {
        if (cappers.equals("null")) {
            HCFactions.getInstance().getCitadelHandler().resetCappers();
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_PURPLE + "Reset Citadel cappers.");
        } else {
            String[] teamNames = cappers.split(",");
            List<ObjectId> teams = new ArrayList<>();

            for (String teamName : teamNames) {
                Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(teamName);

                if (faction != null) {
                    teams.add(faction.getUniqueId());
                } else {
                    sender.sendMessage(ChatColor.RED + "Faction '" + teamName + "' cannot be found.");
                    return;
                }
            }

            HCFactions.getInstance().getCitadelHandler().getCappers().clear();
            HCFactions.getInstance().getCitadelHandler().getCappers().addAll(teams);
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Updated Citadel cappers.");
        }
    }

}