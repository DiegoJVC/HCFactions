package com.cobelpvp.hcfactions.factions.commands.pvp;

import com.cobelpvp.atheneum.command.Command;
import org.bukkit.entity.Player;

public class PvPCommand {

    @Command(names = {"pvptimer", "timer", "pvp"}, permission = "")
    public static void pvp(Player sender) {
        String[] msges = {
                "§c/pvp time - Shows time left on PVP Timer",
                "§c/pvp enable - Remove PVP Timer"};

        sender.sendMessage(msges);
    }

}