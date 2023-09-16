package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdLives {

    @Command(names={ "f lives add", "fac lives add", "faction lives add", "f lives deposit", "f lives d" }, permission="")
    public static void livesAdd(Player sender, @Param(name = "lives") int lives) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);
        if( faction == null ) {
            sender.sendMessage(ChatColor.RED + "You need a faction to use this command.");
            return;
        }

        if( lives <= 0 ) {
            sender.sendMessage(ChatColor.RED + "You really think we'd fall for that?");
            return;
        }

        int currLives = HCFactions.getInstance().getLivesMap().getLives(sender.getUniqueId());

        if( currLives < lives ) {
            sender.sendMessage(ChatColor.RED + "You only have " + ChatColor.YELLOW + currLives + ChatColor.RED + " friend lives, you cannot deposit " + ChatColor.YELLOW + lives);
            return;
        }

        HCFactions.getInstance().getLivesMap().setLives(sender.getUniqueId(), currLives - lives);
        faction.addLives(lives);
        sender.sendMessage(ChatColor.GREEN + "You have deposited " + ChatColor.RED + lives + ChatColor.GREEN + "  friendlives to " + ChatColor.YELLOW + faction.getName() + ChatColor.GREEN + ". You now have " + ChatColor.RED + (currLives - lives) + ChatColor.GREEN + " lives and your faction now has " + ChatColor.RED + faction.getLives() + ChatColor.GREEN + " lives." );
    }

    @Command(names={ "f revive", "fac revive", "faction revive" }, permission="")
    public static void livesRevive(Player sender, @Param(name = "player") UUID whom) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);
        if( faction == null ) {
            sender.sendMessage(ChatColor.RED + "You need a faction to use this command.");
            return;
        }

        if(!faction.isCoLeader(sender.getUniqueId()) && !faction.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only co-leaders and owners can use this command!");
            return;
        }

        if(faction.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "Your faction has no lives to use.");
            return;
        }

        if(!faction.isMember(whom)) {
            sender.sendMessage(ChatColor.RED + "This player is not a member of your faction.");
            return;
        }

        if(!HCFactions.getInstance().getDeathbanMap().isDeathbanned(whom)) {
            sender.sendMessage(ChatColor.RED + "This player is not death banned currently.");
            return;
        }

        faction.removeLives(1);
        HCFactions.getInstance().getDeathbanMap().revive(whom);
        sender.sendMessage(ChatColor.GREEN + "You have revived " + ChatColor.RED + UUIDUtils.name(whom) + ChatColor.GREEN + ".");
    }

    @Command(names={ "f lives", "fac lives", "faction lives" }, permission="")
    public static void getLives(Player sender) {
        Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(sender);
        if( faction == null ) {
            sender.sendMessage(ChatColor.RED + "You need a faction to use this command.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Your faction has " + ChatColor.RED + faction.getLives() + ChatColor.YELLOW + " lives.");
        sender.sendMessage(ChatColor.YELLOW + "To deposit lives, use /t lives add <amount>");
        sender.sendMessage(ChatColor.YELLOW + "Life deposits are FINAL!");
        sender.sendMessage(ChatColor.YELLOW + "Leaders can revive members using " + ChatColor.WHITE + "/t revive <name>");
    }
}
