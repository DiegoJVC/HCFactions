package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class FDToggleCommand {

    @Command(names={ "FD Toggle", "ToggleFoundDiamonds", "ToggleFD" }, permission="")
    public static void fdToggle(Player sender) {
        boolean val = !HCFactions.getInstance().getToggleFoundDiamondsMap().isFoundDiamondToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Found Diamonds messages!");
        HCFactions.getInstance().getToggleFoundDiamondsMap().setFoundDiamondToggled(sender.getUniqueId(), val);
    }

}