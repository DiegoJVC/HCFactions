package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.commands.CmdSetFactionBalance;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdCreate;
import com.cobelpvp.atheneum.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.cobelpvp.hcfactions.factions.menu.DTRMenu;
import com.cobelpvp.hcfactions.factions.menu.DemoteMembersMenu;
import com.cobelpvp.hcfactions.factions.menu.KickPlayersMenu;
import com.cobelpvp.hcfactions.factions.menu.MuteMenu;
import com.cobelpvp.hcfactions.factions.menu.PromoteMembersMenu;
import com.cobelpvp.hcfactions.factions.menu.SelectNewLeaderMenu;
import com.cobelpvp.hcfactions.factions.menu.TeamManageMenu;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class FactionManageCommand {

    @Command(names = {"managefaction leader"}, permission = "hcfactions.manage")
    public static void teamLeader(Player sender, @Param(name = "team") Faction faction) {
        new SelectNewLeaderMenu(faction).openMenu(sender);
    }

    @Command(names = {"managefaction promote"}, permission = "hcfactions.manage")
    public static void promoteTeam(Player sender, @Param(name = "team") Faction faction) {
        new PromoteMembersMenu(faction).openMenu(sender);
    }

    @Command(names = {"managefaction demote"}, permission = "hcfactions.manage")
    public static void demoteTeam(Player sender, @Param(name = "team") Faction faction) {
        new DemoteMembersMenu(faction).openMenu(sender);
    }


    @Command(names = {"managefaction kick"}, permission = "hcfactions.manage")
    public static void kickTeam(Player sender, @Param(name = "team") Faction faction) {
        new KickPlayersMenu(faction).openMenu(sender);
    }


    @Command(names = {"managefaction balance"}, permission = "hcfactions.manage")
    public static void balanceTeam(Player sender, @Param(name = "team") Faction faction) {
        conversationDouble(sender, "§bEnter a new balance for " + faction.getName() + ".", (d) -> {
            CmdSetFactionBalance.setTeamBalance(sender, faction, d.floatValue());
            sender.sendRawMessage(ChatColor.GRAY + faction.getName() + " now has a balance of " + faction.getBalance());
        });
    }

    @Command(names = {"managefaction dtr"}, permission = "hcfactions.manage")
    public static void dtrTeam(Player sender, @Param(name = "team") Faction faction) {
        if (sender.hasPermission("hcfactions.manage.setdtr")) {
            conversationDouble(sender, "§eEnter a new DTR for " + faction.getName() + ".", (d) -> {
                faction.setDTR(d.floatValue());
                sender.sendRawMessage(ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + d.floatValue() + ChatColor.YELLOW + ".");
            });
        } else {
            new DTRMenu(faction).openMenu(sender);
        }
    }

    @Command(names = {"managefaction rename"}, permission = "hcfactions.manage")
    public static void renameTeam(Player sender, @Param(name = "team") Faction faction) {
        conversationString(sender, "§aEnter a new name for " + faction.getName() + ".", (name) -> {
            String oldName = faction.getName();
            faction.rename(name);
            sender.sendRawMessage(ChatColor.GRAY + oldName + " now has a name of " + faction.getName());
        });
    }


    @Command(names = {"managefaction mute"}, permission = "hcfactions.manage")
    public static void muteTeam(Player sender, @Param(name = "team") Faction faction) {
        new MuteMenu(faction).openMenu(sender);

    }


    @Command(names = {"managefaction manage"}, permission = "hcfactions.manage")
    public static void manageTeam(Player sender, @Param(name = "team") Faction faction) {
        new TeamManageMenu(faction).openMenu(sender);
    }

    private static void conversationDouble(Player p, String prompt, Callback<Double> callback) {
        ConversationFactory factory = new ConversationFactory(HCFactions.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                try {
                    callback.callback(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                }

                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }

    private static void conversationString(Player p, String prompt, Callback<String> callback) {
        ConversationFactory factory = new ConversationFactory(HCFactions.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String newName) {

                if (newName.length() > 16) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Maximum faction name size is 16 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (newName.length() < 3) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Minimum faction name size is 3 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (!CmdCreate.ALPHA_NUMERIC.matcher(newName).find()) {
                    if (HCFactions.getInstance().getFactionHandler().getTeam(newName) == null) {
                        callback.callback(newName);
                        return Prompt.END_OF_CONVERSATION;

                    } else {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "A faction with that name already exists!");
                    }
                } else {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Faction names must be alphanumeric!");
                }


                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }
}
