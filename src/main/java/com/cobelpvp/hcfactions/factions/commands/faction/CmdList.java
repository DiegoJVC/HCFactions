package com.cobelpvp.hcfactions.factions.commands.faction;

import mkremins.fanciful.FancyMessage;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CmdList {

    @Command(names={ "f list", "faction list", "fac list" }, permission="")
    public static void teamList(final Player sender, @Param(name="page", defaultValue="1") final int page) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                if (page < 1) {
                    sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
                    return;
                }

                Map<Faction, Integer> teamPlayerCount = new HashMap<>();

                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    if (player.hasMetadata("invisible")) {
                        continue;
                    }

                    Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

                    if (playerFaction != null) {
                        if (teamPlayerCount.containsKey(playerFaction)) {
                            teamPlayerCount.put(playerFaction, teamPlayerCount.get(playerFaction) + 1);
                        } else {
                            teamPlayerCount.put(playerFaction, 1);
                        }
                    }
                }

                int maxPages = (teamPlayerCount.size() / 10) + 1;
                int currentPage = Math.min(page, maxPages);

                LinkedHashMap<Faction, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount);

                int start = (currentPage - 1) * 10;
                int index = 0;

                sender.sendMessage(Faction.GRAY_LINE);
                sender.sendMessage(ChatColor.BLUE + "Faction List " +  ChatColor.GRAY + "(Page " + currentPage + "/" + maxPages + ")");

                for (Map.Entry<Faction, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    index++;

                    if (index < start) {
                        continue;
                    }

                    if (index > start + 10) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();

                    teamMessage.text(index + ". ").color(ChatColor.GRAY).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(ChatColor.YELLOW).tooltip(
                            ChatColor.YELLOW + "DTR: " + teamEntry.getKey().getDTRColor() + Faction.DTR_FORMAT.format(teamEntry.getKey().getDTR()) + ChatColor.YELLOW + " / " + teamEntry.getKey().getMaxDTR() + "\n" +
                            ChatColor.GREEN + "Click to view faction info").command("/f who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(" (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")").color(ChatColor.GREEN);

                    teamMessage.send(sender);
                }

                sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + currentPage + "/" + maxPages + ChatColor.GRAY + ".");
                sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/f list <page#>" + ChatColor.GRAY + ".");
                sender.sendMessage(Faction.GRAY_LINE);
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

    public static LinkedHashMap<Faction, Integer> sortByValues(Map<Faction, Integer> map) {
        LinkedList<java.util.Map.Entry<Faction, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Faction, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Faction, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}