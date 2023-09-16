package com.cobelpvp.hcfactions.factions.subclaim;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.atheneum.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubclaimType implements ParameterType<Subclaim> {

    public Subclaim transform(CommandSender sender, String source) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, players only. :/");
            return (null);
        }

        Player player = (Player) sender;
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(player);

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a faction to execute this command!");
            return (null);
        }

        if (source.equals("location")) {
            Subclaim subclaim = faction.getSubclaim(player.getLocation());

            if (subclaim == null) {
                sender.sendMessage(ChatColor.RED + "You are not inside of a subclaim.");
                return (null);
            }

            return (subclaim);
        }

        Subclaim subclaim = faction.getSubclaim(source);

        if (subclaim == null) {
            sender.sendMessage(ChatColor.RED + "No subclaim with the name " + source + " found.");
            return (null);
        }

        return (subclaim);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);

        if (faction == null) {
            return (completions);
        }


        for (Subclaim subclaim : faction.getSubclaims()) {
            if (StringUtils.startsWithIgnoreCase(subclaim.getName(), source)) {
                completions.add(subclaim.getName());
            }
        }

        return (completions);
    }


}