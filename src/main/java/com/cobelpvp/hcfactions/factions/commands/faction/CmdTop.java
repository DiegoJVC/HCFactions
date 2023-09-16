package com.cobelpvp.hcfactions.factions.commands.faction;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mkremins.fanciful.FancyMessage;
import com.cobelpvp.atheneum.command.Command;

public class CmdTop {

    @Command(names = {"f top", "faction top", "fac top"}, permission = "")
    public static void teamList(final CommandSender sender) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                LinkedHashMap<Faction, Integer> sortedTeamPlayerCount = getSortedTeams();

                int index = 0;

                sender.sendMessage(Faction.GRAY_LINE);

                for (Map.Entry<Faction, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

                    if (teamEntry.getKey().getOwner() == null) {
                        continue;
                    }

                    index++;

                    if (10 <= index) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();

                    Faction faction = teamEntry.getKey();

                    teamMessage.text(index + ". ").color(ChatColor.GRAY).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                            .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED).toString() + teamEntry.getKey().getName() + "\n" +
                                    ChatColor.GREEN + "Leader: " + ChatColor.GRAY + UUIDUtils.name(teamEntry.getKey().getOwner()) + "\n\n" +
                                    ChatColor.GREEN + "Balance: " + ChatColor.GRAY + "$" + faction.getBalance() + "\n" +
                                    ChatColor.GREEN + "Kills: " + ChatColor.GRAY.toString() + faction.getKills() + "\n" +
                                    ChatColor.GREEN + "Deaths: " + ChatColor.GRAY.toString() + faction.getDeaths() + "\n\n" +
                                    ChatColor.GREEN + "KOTH Captures: " + ChatColor.GRAY.toString() + faction.getKothCaptures() + "\n" +
                                    ChatColor.GREEN + "Diamonds Mined: " + ChatColor.GRAY.toString() + faction.getDiamondsMined() + "\n\n" +
                                    ChatColor.GOLD + "Click to view faction info").command("/f who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(" - ").color(ChatColor.YELLOW).then();
                    teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.GRAY);

                    teamMessage.send(sender);
                }

                sender.sendMessage(Faction.GRAY_LINE);
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

    public static LinkedHashMap<Faction, Integer> getSortedTeams() {
        Map<Faction, Integer> teamPointsCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            teamPointsCount.put(faction, faction.getPoints());
        }

        return sortByValues(teamPointsCount);
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
