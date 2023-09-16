package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.util.roads.RoadGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RoadCommand {

    @Command(names = {"roadsgenerate"}, permission = "op")
    public static void generator(Player sender) {
        new RoadGenerator(HCFactions.getInstance(), sender.getWorld().getSpawnLocation(), 15000, 1).run();
        sender.sendMessage(ChatColor.GREEN + ("roads have begun to be generated,please wait around 5 min. (be sure to set the world spawn on this coords -8 62 -8)"));
    }
}
