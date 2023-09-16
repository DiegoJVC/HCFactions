package com.cobelpvp.hcfactions.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import com.cobelpvp.atheneum.command.Command;

public class DammyFixCommand {

    @Command(names={ "dammyisADumbass_longCommandSoNobodyRunsItAccidentally" }, permission="op", async = true, hidden = true)
    public static void eddyIsADumbass_longCommandSoNobodyRunsItAccidentally(Player sender) {
        DBCollection coll = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("TeamActions");
        DBCursor cursor = coll.find();
        Map<String, List<BasicDBObject>> data = new HashMap<>();

        sender.sendMessage("starting database pull");

        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            data.computeIfAbsent(obj.getString("teamId"), i -> new ArrayList<>());
            data.get(obj.getString("teamId")).add(obj);
        }

        sender.sendMessage("Collected data for " + data.size() + " unique factions... starting to sort");

        data.values().forEach(objs -> {
            objs.sort((a, b) -> {
                return a.getDate("time").compareTo(b.getDate("time"));
            });
        });

        sender.sendMessage("sorted factions, starting to remove old");

        data.values().removeIf(e -> {
            sender.sendMessage(e.get(0) + " -> " + e.get(e.size() - 1));
            return e.get(e.size() - 1).getString("type").equals("playerDisbandTeam");
        });

        sender.sendMessage("removed old factions, currently " + data.size() + " are left");

        data.forEach((key, e) -> {
            BasicDBObject latest = e.get(e.size() - 1);
            Faction faction = new Faction(latest.getString("teamName"));
            faction.load((BasicDBObject) latest.get("teamAfterAction"));

            sender.sendMessage(ChatColor.GREEN + "would have Reinstated faction " + faction.getName() + " with " + faction.getClaims().size() + " claims and " + faction.getMembers().size() + " members");
        });

        sender.sendMessage("reinstated all factions");

    }
}