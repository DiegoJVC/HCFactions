package com.cobelpvp.hcfactions.commands;

import java.net.URL;
import java.util.Set;
import java.util.UUID;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.Sets;

import com.cobelpvp.atheneum.command.Command;
import net.minecraft.util.org.apache.commons.io.IOUtils;

public class NullFixCommand {
    
    @Command(names = { "nullfix" }, permission = "hcfactions.nullfix", async = true)
    public static void fixNulls(CommandSender sender) {
        Set<UUID> nullUuids = Sets.newHashSet();
        
        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            for (UUID member : faction.getMembers()) {
                String name = UUIDUtils.name(member);
                if (name == null || name.equals("null")) {
                    nullUuids.add(member);
                }
            }
        }
        
        int fixed = 0;
        
        for (UUID nullUuid : nullUuids) {
            String name = getName(nullUuid);
            if (name != null) {
                if (name.equals("429")) {
                    break;
                }
                
                TeamsUUIDCache.update(nullUuid, name);
                fixed++;
            }
        }
        
        sender.sendMessage(ChatColor.GREEN + "Fixed " + fixed + " UUIDs.");
    }
    
    private static String getName(UUID uuid) {
        String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
        try {
            String nameJson = IOUtils.toString(new URL(url));
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
            String playerSlot = nameValue.get(nameValue.size() - 1).toString();
            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            return nameObject.get("name").toString();
        } catch (Exception e) {
            return "429";
        }
    }
    
}
