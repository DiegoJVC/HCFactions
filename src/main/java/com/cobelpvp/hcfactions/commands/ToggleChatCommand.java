package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class ToggleChatCommand {

    @Command(names={ "ToggleChat", "ToggleGlobalChat", "TGC" }, permission="")
    public static void toggleChat(Player sender) {
        boolean val = !HCFactions.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        HCFactions.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getUniqueId(), val);
    }

}