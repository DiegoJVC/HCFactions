package com.cobelpvp.hcfactions.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.hcfactions.events.listeners.EventActivatedEvent;
import com.cobelpvp.hcfactions.events.listeners.EventCapturedEvent;
import com.cobelpvp.hcfactions.events.listeners.EventDeactivatedEvent;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.hcfactions.events.systemfactions.koth.events.KOTHControlLostEvent;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.serialization.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.cobelpvp.hcfactions.util.InventoryUtils;

public class EventListener implements Listener {

    public EventListener() {
        Bukkit.getLogger().info("Creating indexes...");
        DBCollection mongoCollection = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("KOTHCaptures");
        
        mongoCollection.createIndex(new BasicDBObject("Capper", 1));
        mongoCollection.createIndex(new BasicDBObject("CapperTeam", 1));
        mongoCollection.createIndex(new BasicDBObject("EventName", 1));
        Bukkit.getLogger().info("Creating indexes done.");
    }
    
    @EventHandler
    public void onKOTHActivated(EventActivatedEvent event) {
        if (event.getEvent().isHidden()) {
            return;
        }

        String[] messages;

        switch (event.getEvent().getName()) {
            case "EOTW":
                messages = new String[]{
                        ChatColor.RED + "███████",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "The cap point at spawn",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "is now active.",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "EOTW " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
                        ChatColor.RED + "███████"
                };

                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
                }

                break;
            case "Citadel":
                messages = new String[]{
                        ChatColor.GRAY + "███████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_PURPLE + event.getEvent().getName(),
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "can be contested now.",
                        ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
                        ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                        ChatColor.GRAY + "███████"
                };

                break;

            default:
                messages = new String[] {
                        ColorText.translate("&8[&3&lKOTH&8] &3" + event.getEvent().getName() + " &9can be contested now.")
                };

                break;
        }

        if (event.getEvent().getType() == EventType.DTC) {
            messages = new String[]{
                    ChatColor.RED + "███████",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD + "[Event]",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.YELLOW + "DTC",
                    ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.GOLD + "can be contested now.",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
                    ChatColor.RED + "███████"
            };
        }
        
        final String[] messagesFinal = messages;

        new BukkitRunnable() {

            public void run() {
                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    player.sendMessage(messagesFinal);
                }
            }

        }.runTaskAsynchronously(HCFactions.getInstance());

        for (String message : messages) {
            HCFactions.getInstance().getLogger().info(message);
        }
    }

    @EventHandler
    public void onKOTHCaptured(final EventCapturedEvent event) {
        if (event.getEvent().isHidden()) {
            return;
        }

        final Faction faction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (faction != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + faction.getName() + ChatColor.GOLD + "]";
        }

        final String[] filler = { "", "", "", "", "", "" };
        String[] messages;

        if (event.getEvent().getName().equalsIgnoreCase("Citadel")) {
            messages = new String[] {
                    ChatColor.GRAY + "███████",
                    ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.GOLD + "[Citadel]",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + ChatColor.YELLOW + "controlled by",
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████ " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.GRAY + "█" + ChatColor.DARK_PURPLE + "█" + ChatColor.GRAY + "█████",
                    ChatColor.GRAY + "██" + ChatColor.DARK_PURPLE + "████" + ChatColor.GRAY + "█",
                    ChatColor.GRAY + "███████"
            };
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key Citadel 5 " + event.getPlayer().getName());
        } else if (event.getEvent().getName().equalsIgnoreCase("EOTW")) {
            messages = new String[] {
                    ChatColor.RED + "███████",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[EOTW]",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has been",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "controlled by",
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█",
                    ChatColor.RED + "███████",
            };
        } else if (event.getEvent().getType() == EventType.DTC) {
            messages = new String[] {
                    ChatColor.RED + "███████",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█" + " " + ChatColor.GOLD + "[Event]",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "DTC has been",
                    ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.YELLOW.toString() + ChatColor.BOLD + "controlled by",
                    ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName(),
                    ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█",
                    ChatColor.RED + "███████",
            };
            
            ItemStack rewardKey = InventoryUtils.generateKOTHRewardKey(event.getEvent().getName() + " DTC", 1);
            ItemStack kothSign = HCFactions.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), faction == null ? event.getPlayer().getName() : faction.getName(), EventType.DTC);

            event.getPlayer().getInventory().addItem(rewardKey);
            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(rewardKey)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rewardKey);
            }

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }
        } else {
            messages = new String[] {ColorText.translate("&8[&3&lKOTH&8] &a" + teamName + event.getPlayer().getDisplayName() + " &9captured &3" + event.getEvent().getName() + " &9and received &3KOTH key")};

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key KOTH 4 " + event.getPlayer().getName());
            ItemStack kothSign = HCFactions.getInstance().getServerHandler().generateKOTHSign(event.getEvent().getName(), faction == null ? event.getPlayer().getName() : faction.getName(), EventType.KOTH);
            event.getPlayer().getInventory().addItem(kothSign);

            if (!event.getPlayer().getInventory().contains(kothSign)) {
                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), kothSign);
            }

            Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(event.getPlayer());
            if (playerFaction != null) {
                playerFaction.setKothCaptures(playerFaction.getKothCaptures() + 1);
            }
            
        }

        final String[] messagesFinal = messages;

        new BukkitRunnable() {

            public void run() {
                for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                    player.sendMessage(filler);
                    player.sendMessage(messagesFinal);
                }
            }

        }.runTaskAsynchronously(HCFactions.getInstance());

        for (String message : messages) {
            HCFactions.getInstance().getLogger().info(message);
        }

        final BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("EventName", event.getEvent().getName());
        dbObject.put("EventType", event.getEvent().getType().name());
        dbObject.put("CapturedAt", new Date());
        dbObject.put("Capper", event.getPlayer().getUniqueId().toString().replace("-", ""));
        dbObject.put("CapperTeam", faction == null ? null : faction.getUniqueId().toString());
        if (event.getEvent().getType() == EventType.KOTH) {
            dbObject.put("EventLocation", LocationSerializer.serialize(((KOTH) event.getEvent()).getCapLocation().toLocation(event.getPlayer().getWorld())));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                DBCollection kothCapturesCollection = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("KOTHCaptures");
                kothCapturesCollection.insert(dbObject);
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

    @EventHandler
    public void onKOTHControlLost(final KOTHControlLostEvent event) {
        if (event.getKOTH().getRemainingCapTime() <= (event.getKOTH().getCapTime() - 30)) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendMessage(ColorText.translate("&8[&3&lKOTH&8] &3" + event.getKOTH().getName() + " &9has knocked!"));
            }
        }
    }
}
