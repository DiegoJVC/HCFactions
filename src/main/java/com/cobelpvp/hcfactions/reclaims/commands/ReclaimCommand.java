package com.cobelpvp.hcfactions.reclaims.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ReclaimCommand {

    @Command(names = { "reclaim" }, description = "Reclaim your donator perks", permission = "")
    public static void execute(Player player) {
        if (HCFactions.getInstance().getReclaimHandler().getHasReclaimed().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED.toString() + "You've already used your reclaim!");
            return;
        }

        Configuration config = HCFactions.getInstance().getConfig();

        for (String key : config.getConfigurationSection("reclaims").getKeys(false).stream().sorted(Comparator.comparingInt(key -> (int) config.getLong("reclaims." + key + ".priority", 99L))).collect(Collectors.toList())) {
            if (player.hasPermission(config.getString("reclaims." + key + ".permission"))) {
                HCFactions.getInstance().getReclaimHandler().getHasReclaimed().add(player.getUniqueId());

                for (String command : config.getStringList("reclaims." + key + ".commands")) {
                    try {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
                    } catch (Exception e) {
                        HCFactions.getInstance().getLogger().severe("[Reclaims] Failed to execute command: " + command + " for player " + player.getName());
                        e.printStackTrace();
                    }
                }

                player.sendMessage(ChatColor.GREEN.toString() + "You've successfully obtained your reclaim " + ChatColor.BOLD + key);
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "You have nothing to reclaim!");
    }

}
