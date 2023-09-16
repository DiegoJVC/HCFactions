package com.cobelpvp.hcfactions.factions;

import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.hcfactions.HCFactions;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class HCFactionsConstants {

    public static String teamChatFormat(Player player, String message) {
        return ChatColor.YELLOW + "(" + ChatColor.DARK_GREEN + "FC" + ChatColor.YELLOW + ")" + ChatColor.DARK_GREEN + player.getName() + ChatColor.GRAY + ": " + ChatColor.GREEN + message;
    }

    public static String teamChatSpyFormat(Faction faction, Player player, String message) {
        return (ChatColor.GOLD + "[" + ChatColor.DARK_AQUA + "TC: " + ChatColor.YELLOW + faction.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_AQUA + player.getName() + ": " + message);
    }

    public static String allyChatFormat(Player player, String message) {
        return ChatColor.YELLOW + "(" + ChatColor.LIGHT_PURPLE + "AC" + ChatColor.YELLOW + ")" + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + message;
    }

    public static String allyChatSpyFormat(Faction faction, Player player, String message) {
        return (ChatColor.GOLD + "[" + Faction.ALLY_COLOR + "AC: " + ChatColor.YELLOW + faction.getName() + ChatColor.GOLD + "]" + Faction.ALLY_COLOR + player.getName() + ": " + message);
    }

    public static String getCenter(String message) {
        return ColorText.translate("&6&m" + StringUtils.repeat("-", 9) + "&r&6[&2" + message + "&6]&6&m" + StringUtils.repeat("-", 9));
    }

    public static String publicChatFormat(Faction faction, String rankPrefix) {
        String starting = "";

        if (faction != null) {
            if (rankPrefix.toLowerCase().contains("famous") || rankPrefix.toLowerCase().contains("youtube")) {
                rankPrefix = "";
            }

            starting = ChatColor.GOLD + "[" + HCFactions.getInstance().getServerHandler().getDefaultRelationColor() + faction.getName() + ChatColor.GOLD + "]";
        } else {
            starting = ChatColor.GOLD + "[]";
        }

        return starting + rankPrefix + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s";
    }



}