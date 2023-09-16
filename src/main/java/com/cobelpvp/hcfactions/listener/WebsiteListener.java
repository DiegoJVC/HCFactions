package com.cobelpvp.hcfactions.listener;

import java.util.Date;
import java.util.UUID;
import com.cobelpvp.atheneum.serialization.PlayerInventorySerializer;
import com.cobelpvp.atheneum.util.PlayerUtils;
import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class WebsiteListener implements Listener {

    public WebsiteListener() {
        Bukkit.getLogger().info("Creating indexes...");
        DBCollection mongoCollection = HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("Deaths");
        
        mongoCollection.createIndex(new BasicDBObject("uuid", 1));
        mongoCollection.createIndex(new BasicDBObject("killerUUID", 1));
        mongoCollection.createIndex(new BasicDBObject("ip", 1));
        Bukkit.getLogger().info("Creating indexes done.");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final BasicDBObject playerDeath = new BasicDBObject();

        playerDeath.put("_id", UUID.randomUUID().toString().substring(0, 7));
        
        if (event.getEntity().getKiller() != null) {
            playerDeath.append("healthLeft", (int) event.getEntity().getKiller().getHealth());
            playerDeath.append("killerUUID", event.getEntity().getKiller().getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerLastUsername", event.getEntity().getKiller().getName());
            playerDeath.append("killerInventory", PlayerInventorySerializer.getInsertableObject(event.getEntity().getKiller()));
            playerDeath.append("killerPing", PlayerUtils.getPing(event.getEntity().getKiller()));
            playerDeath.append("killerHunger", event.getEntity().getKiller().getFoodLevel());
        } else {
            try{
                playerDeath.append("reason", event.getEntity().getLastDamageCause().getCause().toString());
            } catch (NullPointerException ignored) {}
        }

        playerDeath.append("playerInventory", PlayerInventorySerializer.getInsertableObject(event.getEntity()));
        playerDeath.append("ip", event.getEntity().getAddress().toString().split(":")[0].replace("/", ""));
        playerDeath.append("uuid", event.getEntity().getUniqueId().toString().replace("-", ""));
        playerDeath.append("lastUsername", event.getEntity().getName());
        playerDeath.append("hunger", event.getEntity().getFoodLevel());
        playerDeath.append("ping", PlayerUtils.getPing(event.getEntity()));
        playerDeath.append("when", new Date());

        new BukkitRunnable() {

            public void run() {
                HCFactions.getInstance().getMongoPool().getDB(HCFactions.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(HCFactions.getInstance());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
            case LAPIS_ORE:
            case EMERALD_ORE:
                event.getBlock().setMetadata("PlacedByPlayer", new FixedMetadataValue(HCFactions.getInstance(), true));
                break;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if ((event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) || event.getBlock().hasMetadata("PlacedByPlayer")) {
            return;
        }

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                HCFactions.getInstance().getDiamondMinedMap().setMined(event.getPlayer(), HCFactions.getInstance().getDiamondMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case GOLD_ORE:
                HCFactions.getInstance().getGoldMinedMap().setMined(event.getPlayer().getUniqueId(), HCFactions.getInstance().getGoldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case IRON_ORE:
                HCFactions.getInstance().getIronMinedMap().setMined(event.getPlayer().getUniqueId(), HCFactions.getInstance().getIronMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case COAL_ORE:
                HCFactions.getInstance().getCoalMinedMap().setMined(event.getPlayer().getUniqueId(), HCFactions.getInstance().getCoalMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                HCFactions.getInstance().getRedstoneMinedMap().setMined(event.getPlayer().getUniqueId(), HCFactions.getInstance().getRedstoneMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case LAPIS_ORE:
                HCFactions.getInstance().getLapisMinedMap().setMined(event.getPlayer().getUniqueId(), HCFactions.getInstance().getLapisMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case EMERALD_ORE:
                HCFactions.getInstance().getEmeraldMinedMap().setMined(event.getPlayer().getUniqueId(), HCFactions.getInstance().getEmeraldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
        }
    }

}