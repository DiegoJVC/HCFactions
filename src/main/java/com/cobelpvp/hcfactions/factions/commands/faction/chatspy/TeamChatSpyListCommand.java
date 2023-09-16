package com.cobelpvp.hcfactions.factions.commands.faction.chatspy;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyListCommand {

    @Command(names = {"f chatspy list", "faction chatspy list", "fac chatspy list"}, permission = "hcfactions.chatspy")
    public static void teamChatSpyList(Player sender) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ObjectId team : HCFactions.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId())) {
            Faction factionObj = HCFactions.getInstance().getFactionHandler().getTeam(team);

            if (factionObj != null) {
                stringBuilder.append(ChatColor.YELLOW).append(factionObj.getName()).append(ChatColor.GOLD).append(", ");
            }
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the faction chat of: " + stringBuilder.toString());
    }

}