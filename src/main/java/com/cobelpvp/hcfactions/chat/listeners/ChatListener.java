package com.cobelpvp.hcfactions.chat.listeners;

import com.cobelpvp.hcfactions.chat.enums.ChatMode;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdMute;
import com.cobelpvp.hcfactions.factions.track.TeamActionTracker;
import com.cobelpvp.hcfactions.factions.track.TeamActionType;
import com.google.common.collect.ImmutableMap;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.HCFactionsConstants;
import com.cobelpvp.hcfactions.chat.ChatHandler;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdShadowMute;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEarly(AsyncPlayerChatEvent event) {
        ChatMode playerChatMode = HCFactions.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            event.getPlayer().setMetadata("NoSpamCheck", new FixedMetadataValue(HCFactions.getInstance(), true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().removeMetadata("NoSpamCheck", HCFactions.getInstance());

        Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());
        String rankPrefix = event.getPlayer().hasMetadata("RankPrefix") ? event.getPlayer().getMetadata("RankPrefix").get(0).asString() : "";
        ChatMode playerChatMode = HCFactions.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        if (forcedChatMode != null) {
            event.setMessage(event.getMessage().substring(1).trim());
        }

        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        if (event.isCancelled() && finalChatMode == ChatMode.PUBLIC) {
            return;
        }

        event.setCancelled(true);

        if (finalChatMode != ChatMode.PUBLIC && playerFaction == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a faction!");
            return;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            if (playerFaction == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a faction!");
                return;
            } else if (finalChatMode == ChatMode.OFFICER && !playerFaction.isCaptain(event.getPlayer().getUniqueId()) && !playerFaction.isCoLeader(event.getPlayer().getUniqueId()) && !playerFaction.isOwner(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in officer chat if you're not an officer!");
                return;
            }
        }

        switch (finalChatMode) {
            case PUBLIC:
                if (CmdMute.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Your faction is muted!");
                    return;
                }

                String publicChatFormat = HCFactionsConstants.publicChatFormat(playerFaction, rankPrefix);

                String finalMessage = String.format(publicChatFormat, event.getPlayer().getDisplayName(), event.getMessage());

                for (Player player : event.getRecipients()) {
                    if (playerFaction == null) {
                        if (CmdShadowMute.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                            continue;
                        }

                        if (event.getPlayer().isOp() || HCFactions.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                            player.sendMessage(finalMessage);
                        }
                    } else {
                        if (playerFaction.isMember(player.getUniqueId())) {
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + HCFactions.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + ChatColor.DARK_GREEN));
                        } else if (playerFaction.isAlly(player.getUniqueId())) {
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + HCFactions.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + Faction.ALLY_COLOR));
                        } else {
                            if (CmdShadowMute.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                                continue;
                            }

                            if (event.getPlayer().isOp() || HCFactions.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                                player.sendMessage(finalMessage);
                            }
                        }
                    }
                }

                ChatHandler.getPublicMessagesSent().incrementAndGet();
                HCFactions.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
                break;
            case ALLIANCE:
                String allyChatFormat = HCFactionsConstants.allyChatFormat(event.getPlayer(), event.getMessage());
                String allyChatSpyFormat = HCFactionsConstants.allyChatSpyFormat(playerFaction, event.getPlayer(), event.getMessage());

                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    if (playerFaction.isMember(player.getUniqueId()) || playerFaction.isAlly(player.getUniqueId())) {
                        player.sendMessage(allyChatFormat);
                    } else if (HCFactions.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerFaction.getUniqueId())) {
                        player.sendMessage(allyChatSpyFormat);
                    }
                }

                for (ObjectId allyId : playerFaction.getAllies()) {
                    Faction ally = HCFactions.getInstance().getFactionHandler().getTeam(allyId);

                    if (ally != null) {
                        TeamActionTracker.logActionAsync(ally, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.<String, Object>builder()
                                .put("allyTeamId", playerFaction.getUniqueId())
                                .put("allyTeamName", playerFaction.getName())
                                .put("playerId", event.getPlayer().getUniqueId())
                                .put("playerName", event.getPlayer().getName())
                                .put("message", event.getMessage())
                                .build()
                        );
                    }
                }

                TeamActionTracker.logActionAsync(playerFaction, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));

                HCFactions.getInstance().getServer().getLogger().info("[Ally Chat] [" + playerFaction.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case TEAM:
                String teamChatFormat = HCFactionsConstants.teamChatFormat(event.getPlayer(), event.getMessage());
                String teamChatSpyFormat = HCFactionsConstants.teamChatSpyFormat(playerFaction, event.getPlayer(), event.getMessage());

                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    if (playerFaction.isMember(player.getUniqueId())) {
                        player.sendMessage(teamChatFormat);
                    } else if (HCFactions.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerFaction.getUniqueId())) {
                        player.sendMessage(teamChatSpyFormat);
                    }
                }

                TeamActionTracker.logActionAsync(playerFaction, TeamActionType.TEAM_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));

                HCFactions.getInstance().getServer().getLogger().info("[Faction Chat] [" + playerFaction.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
        }
    }

}