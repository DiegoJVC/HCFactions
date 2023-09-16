package com.cobelpvp.hcfactions.factions;

import com.cobelpvp.atheneum.command.ParameterType;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.HCFactions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FactionType implements ParameterType<Faction> {

    public Faction transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(((Player) sender).getUniqueId());

            if (faction == null) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have faction right now.");
                return (null);
            }

            return (faction);
        }

        Faction byName = HCFactions.getInstance().getFactionHandler().getTeam(source);

        if (byName != null) {
            return (byName);
        }

        Player bukkitPlayer = HCFactions.getInstance().getServer().getPlayer(source);

        if (bukkitPlayer != null) {
            Faction byMemberBukkitPlayer = HCFactions.getInstance().getFactionHandler().getTeam(bukkitPlayer.getUniqueId());

            if (byMemberBukkitPlayer != null) {
                return (byMemberBukkitPlayer);
            }
        }

        Faction byMemberUUID = HCFactions.getInstance().getFactionHandler().getTeam(UUIDUtils.uuid(source));

        if (byMemberUUID != null) {
            return (byMemberUUID);
        }

        sender.sendMessage(ChatColor.RED + "No faction or member with the name " + source + " found.");
        return (null);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        // Teams being included in the completion is ENABLED by default.
        if (!flags.contains("noteams")) {
            for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
                if (StringUtils.startsWithIgnoreCase(faction.getName(), source)) {
                    completions.add(faction.getName());
                }
            }
        }

        // Players being included in the completion is DISABLED by default.
        if (flags.contains("players")) {
            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                    completions.add(player.getName());
                }
            }
        }

        return (completions);
    }

}