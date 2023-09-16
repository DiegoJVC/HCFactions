package com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.GlowMountain;
import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.GlowHandler;
import com.cobelpvp.atheneum.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class GlowCommand {

    @Command(names = "glow scan", permission = "hcfactions.glowstone.admin")
    public static void glowScan(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(GlowHandler.getGlowTeamName());

        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the faction (" + GlowHandler.getGlowTeamName() + ") and claim it!");
            return;
        }

        if (faction.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + GlowHandler.getGlowTeamName() + "' before scanning it!");
            return;
        }

        if (!HCFactions.getInstance().getGlowHandler().hasGlowMountain()) {
            HCFactions.getInstance().getGlowHandler().setGlowMountain(new GlowMountain());
        }

        HCFactions.getInstance().getGlowHandler().getGlowMountain().scan();
        HCFactions.getInstance().getGlowHandler().save(); // save to file :D

        sender.sendMessage(GREEN + "[Glowstone Mountain] Scanned all glowstone and saved glowstone mountain to file!");
    }

    @Command(names = "glow reset", permission = "op")
    public static void glowReset(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(GlowHandler.getGlowTeamName());

        if (faction == null || faction.getClaims().isEmpty() || !HCFactions.getInstance().getGlowHandler().hasGlowMountain()) {
            sender.sendMessage(RED + "Create the faction '" + GlowHandler.getGlowTeamName() + "', then make a claim for it, finally scan it! (/glow scan)");
            return;
        }

        HCFactions.getInstance().getGlowHandler().getGlowMountain().reset();

        Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + GREEN + " All glowstone has been reset!");
    }
}