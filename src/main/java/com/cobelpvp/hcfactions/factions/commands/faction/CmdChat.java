package com.cobelpvp.hcfactions.factions.commands.faction;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.chat.enums.ChatMode;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdChat {

    @Command(names={"f chat", "faction chat", "fac chat", "f c", "faction c", "fac c", "mc" }, permission="")
    public static void teamChat(Player sender, @Param(name="chat mode", defaultValue="toggle") String chatMode) {
        ChatMode parsedChatMode = null;

        if (chatMode.equalsIgnoreCase("t") || chatMode.equalsIgnoreCase("team") || chatMode.equalsIgnoreCase("f") || chatMode.equalsIgnoreCase("fac") || chatMode.equalsIgnoreCase("faction") || chatMode.equalsIgnoreCase("fc")) {
            parsedChatMode = ChatMode.TEAM;
        } else if (chatMode.equalsIgnoreCase("g") || chatMode.equalsIgnoreCase("p") || chatMode.equalsIgnoreCase("global") || chatMode.equalsIgnoreCase("public") || chatMode.equalsIgnoreCase("gc")) {
            parsedChatMode = ChatMode.PUBLIC;
        } else if (chatMode.equalsIgnoreCase("a") || chatMode.equalsIgnoreCase("allies") || chatMode.equalsIgnoreCase("ally") || chatMode.equalsIgnoreCase("alliance") || chatMode.equalsIgnoreCase("ac")) {
            parsedChatMode = ChatMode.ALLIANCE;
        } else if (chatMode.equalsIgnoreCase("captain") || chatMode.equalsIgnoreCase("officer") || chatMode.equalsIgnoreCase("o") || chatMode.equalsIgnoreCase("c") || chatMode.equalsIgnoreCase("oc")) {
            parsedChatMode = ChatMode.OFFICER;
        }

        setChat(sender, parsedChatMode);
    }

    @Command(names={ "fc", "tc" }, permission="")
    public static void fc(Player sender) {
        setChat(sender, ChatMode.TEAM);
    }

    @Command(names={ "gc", "pc" }, permission="")
    public static void gc(Player sender) {
        setChat(sender, ChatMode.PUBLIC);
    }

    @Command(names={ "oc" }, permission="")
    public static void oc(Player sender) {
        setChat(sender, ChatMode.OFFICER);
    }

    private static void setChat(Player player, ChatMode chatMode) {
        if (chatMode != null) {
            Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);

            if (chatMode != ChatMode.PUBLIC) {
                if (playerFaction == null) {
                    player.sendMessage(ChatColor.RED + "You must be on a faction to use this chat mode.");
                    return;
                } else if (chatMode == ChatMode.OFFICER && !playerFaction.isCaptain(player.getUniqueId()) && !playerFaction.isCoLeader(player.getUniqueId()) && !playerFaction.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You must be an officer or above in your faction to use this chat mode.");
                    return;
                }
            }

            switch (chatMode) {
                case PUBLIC:
                    player.sendMessage(ChatColor.YELLOW + "Public chat mode.");
                    break;
                case ALLIANCE:
                    player.sendMessage(ChatColor.YELLOW + "Alliance only chat mode.");
                    break;
                case TEAM:
                    player.sendMessage(ChatColor.YELLOW + "Faction only chat mode.");
                    break;
                case OFFICER:
                    player.sendMessage(ChatColor.YELLOW + "Officer only chat mode.");
                    break;
            }

            HCFactions.getInstance().getChatModeMap().setChatMode(player.getUniqueId(), chatMode);
        } else {
            switch (HCFactions.getInstance().getChatModeMap().getChatMode(player.getUniqueId())) {
                case PUBLIC:
                    Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(player);
                    boolean teamHasAllies = faction != null && faction.getAllies().size() > 0;

                    setChat(player, teamHasAllies ? ChatMode.ALLIANCE : ChatMode.TEAM);
                    break;
                case ALLIANCE:
                    setChat(player, ChatMode.TEAM);
                    break;
                case TEAM:
                    Faction faction2 = HCFactions.getInstance().getFactionHandler().getTeam(player);
                    boolean isOfficer = faction2 != null && (faction2.isCaptain(player.getUniqueId()) || faction2.isCoLeader(player.getUniqueId()) || faction2.isOwner(player.getUniqueId()));

                    setChat(player, isOfficer ? ChatMode.OFFICER : ChatMode.PUBLIC);
                    break;
                case OFFICER:
                    setChat(player, ChatMode.PUBLIC);
                    break;
            }
        }
    }

}