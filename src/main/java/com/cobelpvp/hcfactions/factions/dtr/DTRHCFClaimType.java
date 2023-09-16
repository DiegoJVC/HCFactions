package com.cobelpvp.hcfactions.factions.dtr;

import com.cobelpvp.atheneum.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DTRHCFClaimType implements ParameterType<DTRHCFClaim> {

    public DTRHCFClaim transform(CommandSender sender, String source) {
        for (DTRHCFClaim hcfclaimType : DTRHCFClaim.values()) {
            if (source.equalsIgnoreCase(hcfclaimType.getName())) {
                return (hcfclaimType);
            }
        }

        sender.sendMessage(ChatColor.RED + "No hcfclaim type with the name " + source + " found.");
        return (null);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (DTRHCFClaim hcfclaim : DTRHCFClaim.values()) {
            if (StringUtils.startsWithIgnoreCase(hcfclaim.getName(), source)) {
                completions.add(hcfclaim.getName());
            }
        }

        return (completions);
    }

}