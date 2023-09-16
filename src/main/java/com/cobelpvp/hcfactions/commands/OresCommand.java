package com.cobelpvp.hcfactions.commands;

import java.util.UUID;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class OresCommand {

    @Command(names={ "Ores" }, permission="")
    public static void ores(Player sender, @Param(name="player") UUID player) {
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + HCFactions.getInstance().getDiamondMinedMap().getMined(player));
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + HCFactions.getInstance().getEmeraldMinedMap().getMined(player));
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + HCFactions.getInstance().getRedstoneMinedMap().getMined(player));
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + HCFactions.getInstance().getGoldMinedMap().getMined(player));
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + HCFactions.getInstance().getIronMinedMap().getMined(player));
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + HCFactions.getInstance().getLapisMinedMap().getMined(player));
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + HCFactions.getInstance().getCoalMinedMap().getMined(player));
    }

}