package com.cobelpvp.hcfactions.factions.commands.faction.chatspy;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamChatSpyClearCommand {

    @Command(names = {"f chatspy clear", "faction chatspy clear", "fac chatspy clear"}, permission = "hcfactions.chatspy")
    public static void teamChatSpyClear(Player sender) {
        HCFactions.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), new ArrayList<ObjectId>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any factions.");
    }

}