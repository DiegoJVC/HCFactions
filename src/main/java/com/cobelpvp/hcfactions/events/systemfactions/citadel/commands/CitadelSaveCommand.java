package com.cobelpvp.hcfactions.events.systemfactions.citadel.commands;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelSaveCommand {

    @Command(names={"citadel save"}, permission="hcfactions.citadel.admin")
    public static void citadelSave(Player sender) {
        HCFactions.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Saved Citadel info to file.");
    }

}