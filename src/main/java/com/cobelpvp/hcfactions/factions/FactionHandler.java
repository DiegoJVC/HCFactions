package com.cobelpvp.hcfactions.factions;

import com.cobelpvp.hcfactions.factions.claims.Subclaim;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaimType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.subclaim.SubclaimType;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.command.TeamsCommandHandler;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FactionHandler {
    
    private Map<String, Faction> teamNameMap = new ConcurrentHashMap<>(); // Team Name -> Team
    private Map<ObjectId, Faction> teamUniqueIdMap = new ConcurrentHashMap<>(); // Team UUID -> Team
    private Map<UUID, Faction> playerTeamMap = new ConcurrentHashMap<>(); // Player UUID -> Team
    @Getter
    @Setter
    private boolean rostersLocked = false;
    
    public FactionHandler() {
        TeamsCommandHandler.registerParameterType(Faction.class, new FactionType());
        TeamsCommandHandler.registerParameterType(DTRHCFClaim.class, new DTRHCFClaimType());
        TeamsCommandHandler.registerParameterType(Subclaim.class, new SubclaimType());
        
        // Load teams from Redis.
        Atheneum.getInstance().runRedisCommand(redis -> {
            for (String key : redis.keys("hcf_teams.*")) {
                String loadString = redis.get(key);

                try {
                    Faction faction = new Faction(key.split("\\.")[1]);
                    faction.load(loadString);

                    setupTeam(faction);
                } catch (Exception e) {
                    e.printStackTrace();
                    HCFactions.getInstance().getLogger().severe("Could not load faction from raw string: " + loadString);
                }
            }

            rostersLocked = Boolean.valueOf(redis.get("RostersLocked"));
            return (null);
        });
        
        Bukkit.getLogger().info("Creating indexes...");

        MongoCollection<Document> playerCollection = HCFactions.getInstance().getMongoPool().getDatabase(HCFactions.MONGO_DB_NAME).getCollection("Players");
        playerCollection.createIndex(new BasicDBObject("Team", 1));

        MongoCollection<Document> teamCollection = HCFactions.getInstance().getMongoPool().getDatabase(HCFactions.MONGO_DB_NAME).getCollection("Teams");
        teamCollection.createIndex(new BasicDBObject("Owner", 1));
        teamCollection.createIndex(new BasicDBObject("CoLeaders", 1));
        teamCollection.createIndex(new BasicDBObject("Captains", 1));
        teamCollection.createIndex(new BasicDBObject("Members", 1));
        teamCollection.createIndex(new BasicDBObject("Name", 1));

        Bukkit.getLogger().info("Creating indexes done.");
    }
    
    public List<Faction> getTeams() {
        return (new ArrayList<>(teamNameMap.values()));
    }
    
    public Faction getTeam(String teamName) {
        return (teamNameMap.get(teamName.toLowerCase()));
    }
    
    public Faction getTeam(ObjectId teamUUID) {
        return (teamUUID == null ? null : teamUniqueIdMap.get(teamUUID));
    }
    
    public Faction getTeam(UUID playerUUID) {
        return (playerUUID == null ? null : playerTeamMap.get(playerUUID));
    }
    
    public Faction getTeam(Player player) {
        return (getTeam(player.getUniqueId()));
    }
    
    public void setTeam(UUID playerUUID, Faction faction, boolean update) {
        if (faction == null) {
            playerTeamMap.remove(playerUUID);
        } else {
            playerTeamMap.put(playerUUID, faction);
        }
        
        if (update) {
            Bukkit.getScheduler().runTaskAsynchronously(HCFactions.getInstance(), () -> {
                // update their team in mongo
                DBCollection playersCollection = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("Players");
                BasicDBObject player = new BasicDBObject("_id", playerUUID.toString().replace("-", ""));
                
                if (faction != null) {
                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", faction.getUniqueId().toHexString())));
                } else {
                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", null)));
                }
            });
        }
    }
    
    public void setTeam(UUID playerUUID, Faction faction) {
        setTeam(playerUUID, faction, true); // standard cases we do update mongo
    }
    
    public void setupTeam(Faction faction) {
        setupTeam(faction, false);
    }
    
    public void setupTeam(Faction faction, boolean update) {
        teamNameMap.put(faction.getName().toLowerCase(), faction);
        teamUniqueIdMap.put(faction.getUniqueId(), faction);
        
        for (UUID member : faction.getMembers()) {
            setTeam(member, faction, update); // no need to update mongo!
        }
    }
    
    public void removeTeam(Faction faction) {
        teamNameMap.remove(faction.getName().toLowerCase());
        teamUniqueIdMap.remove(faction.getUniqueId());
        
        for (UUID member : faction.getMembers()) {
            setTeam(member, null);
        }
    }
    
    public void recachePlayerTeams() {
        playerTeamMap.clear();
        
        for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
            for (UUID member : faction.getMembers()) {
                setTeam(member, faction);
            }
        }
    }
    
}