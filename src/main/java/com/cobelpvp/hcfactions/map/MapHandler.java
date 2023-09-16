package com.cobelpvp.hcfactions.map;

import com.cobelpvp.hcfactions.server.Deathban;
import com.cobelpvp.hcfactions.server.ServerHandler;
import com.cobelpvp.hcfactions.util.nametag.HCFactionsNametagProvider;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.nametag.TeamsNametagHandler;
import com.cobelpvp.atheneum.scoreboard.TeamsScoreboardHandler;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.listener.BorderListener;
import com.cobelpvp.hcfactions.scoreboard.HCFactionsScoreboardConfiguration;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class MapHandler {

    private transient File mapInfo;
    @Getter
    private int allyLimit;
    @Getter
    private int teamSize;
    @Getter
    private long regenTimeDeath;
    @Getter
    private long regenTimeRaidable;
    @Getter
    private String mapStartedString;
    @Getter
    private double baseLootingMultiplier;
    @Getter
    private double level1LootingMultiplier;
    @Getter
    private double level2LootingMultiplier;
    @Getter
    private double level3LootingMultiplier;
    @Getter
    private boolean craftingGopple;
    @Getter
    private boolean craftingReducedMelon;
    @Getter
    private int goppleCooldown;
    @Getter
    private String endPortalLocation;
    @Getter
    @Setter
    private int netherBuffer;
    @Getter
    @Setter
    private int worldBuffer;
    @Getter
    private float dtrIncrementMultiplier;

    public MapHandler() {
    }

    public void load() {
        reloadConfig();

        TeamsNametagHandler.registerProvider(new HCFactionsNametagProvider());
        TeamsScoreboardHandler.setConfiguration(HCFactionsScoreboardConfiguration.create());
        TeamsEconomyHandler.init();

        Iterator<Recipe> recipeIterator = HCFactions.getInstance().getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            if (!craftingGopple && recipe.getResult().getDurability() == (short) 1 && recipe.getResult().getType() == org.bukkit.Material.GOLDEN_APPLE) {
                recipeIterator.remove();
            }

            if (craftingReducedMelon && recipe.getResult().getType() == Material.SPECKLED_MELON) {
                recipeIterator.remove();
            }

            if (recipe.getResult().getType() == Material.EXPLOSIVE_MINECART) {
                recipeIterator.remove();
            }
        }

        if (craftingReducedMelon) {
            HCFactions.getInstance().getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON)).addIngredient(Material.MELON).addIngredient(Material.GOLD_NUGGET));
        }

        ShapedRecipe nametagRecipe = new ShapedRecipe(new ItemStack(Material.NAME_TAG));
        ShapedRecipe saddleRecipe = new ShapedRecipe(new ItemStack(Material.SADDLE));
        ShapedRecipe horseArmorRecipe = new ShapedRecipe(new ItemStack(Material.DIAMOND_BARDING));

        nametagRecipe.shape(" I ",
                " P ",
                " S ");
        nametagRecipe.setIngredient('I', Material.INK_SACK);
        nametagRecipe.setIngredient('P', Material.PAPER);
        nametagRecipe.setIngredient('S', Material.STRING);

        saddleRecipe.shape("  L",
                "LLL",
                "B B");
        saddleRecipe.setIngredient('L', Material.LEATHER);
        saddleRecipe.setIngredient('B', Material.LEASH);

        horseArmorRecipe.shape(" SD",
                "BBL",
                "LL ");
        horseArmorRecipe.setIngredient('S', Material.SADDLE);
        horseArmorRecipe.setIngredient('D', Material.DIAMOND);
        horseArmorRecipe.setIngredient('B', Material.DIAMOND_BLOCK);
        horseArmorRecipe.setIngredient('L', Material.LEATHER);

        HCFactions.getInstance().getServer().addRecipe(nametagRecipe);
        HCFactions.getInstance().getServer().addRecipe(saddleRecipe);
        HCFactions.getInstance().getServer().addRecipe(horseArmorRecipe);
    }

    public void reloadConfig() {
        try {
            mapInfo = new File(HCFactions.getInstance().getDataFolder(), "mapInfo.json");

            if (!mapInfo.exists()) {
                mapInfo.createNewFile();

                BasicDBObject dbObject = getDefaults();

                FileUtils.write(mapInfo, Atheneum.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            } else {
                BasicDBObject file = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

                BasicDBObject defaults = getDefaults();

                defaults.keySet().stream().filter(key -> !file.containsKey(key)).forEach(key -> file.put(key, defaults.get(key)));

                FileUtils.write(mapInfo, Atheneum.GSON.toJson(new JsonParser().parse(file.toString())));
            }

            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                this.allyLimit = dbObject.getInt("allyLimit", 0);
                this.teamSize = dbObject.getInt("teamSize", 15);
                this.regenTimeDeath = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeDeath", 25));
                this.regenTimeRaidable = TimeUnit.MINUTES.toMillis(dbObject.getInt("regenTimeRaidable", 30));
                this.mapStartedString = dbObject.getString("mapStartedString");
                ServerHandler.WARZONE_RADIUS = dbObject.getInt("warzone", 1000);
                BorderListener.BORDER_SIZE = dbObject.getInt("border", 2500);
                this.goppleCooldown = dbObject.getInt("goppleCooldown");
                this.netherBuffer = dbObject.getInt("netherBuffer");
                this.worldBuffer = dbObject.getInt("worldBuffer");
                this.endPortalLocation = dbObject.getString("endPortalLocation");

                BasicDBObject looting = (BasicDBObject) dbObject.get("looting");

                this.baseLootingMultiplier = looting.getDouble("base");
                this.level1LootingMultiplier = looting.getDouble("level1");
                this.level2LootingMultiplier = looting.getDouble("level2");
                this.level3LootingMultiplier = looting.getDouble("level3");

                BasicDBObject crafting = (BasicDBObject) dbObject.get("crafting");

                this.craftingGopple = crafting.getBoolean("gopple");
                this.craftingReducedMelon = crafting.getBoolean("reducedMelon");

                if (dbObject.containsKey("deathban")) {
                    BasicDBObject deathban = (BasicDBObject) dbObject.get("deathban");

                    Deathban.load(deathban);
                }

                this.dtrIncrementMultiplier = (float) dbObject.getDouble("dtrIncrementMultiplier", 4.5F);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BasicDBObject getDefaults() {
        BasicDBObject dbObject = new BasicDBObject();

        BasicDBObject looting = new BasicDBObject();
        BasicDBObject crafting = new BasicDBObject();
        BasicDBObject deathban = new BasicDBObject();

        dbObject.put("allyLimit", 0);
        dbObject.put("teamSize", 5);
        dbObject.put("regenTimeDeath", 25);
        dbObject.put("regenTimeRaidable", 30);
        dbObject.put("mapStartedString", "Map 1 - Started November 11, 2022");
        dbObject.put("warzone", 1000);
        dbObject.put("netherBuffer", 200);
        dbObject.put("worldBuffer", 300);
        dbObject.put("endPortalLocation", "1000, 1000");
        dbObject.put("border", 2500);
        dbObject.put("goppleCooldown", TimeUnit.HOURS.toMinutes(4));

        looting.put("base", 1D);
        looting.put("level1", 1.2D);
        looting.put("level2", 1.4D);
        looting.put("level3", 2D);
        looting.put("level4", 3.5D);

        dbObject.put("looting", looting);

        crafting.put("gopple", true);
        crafting.put("reducedMelon", true);

        dbObject.put("crafting", crafting);

        deathban.put("Famous", 3);
        deathban.put("Youtube", 6);
        deathban.put("Streamer", 9);
        deathban.put("Media", 9);
        deathban.put("Cobel", 3);
        deathban.put("Inmortal", 6);
        deathban.put("Legend", 9);
        deathban.put("Ancient", 12);
        deathban.put("Archon", 15);
        deathban.put("Basic", 18);
        deathban.put("DEFAULT", 20);

        dbObject.put("deathban", deathban);

        dbObject.put("dtrIncrementMultiplier", 11.5F);
        return dbObject;
    }

    public void saveBorder() {
        try {
            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                dbObject.put("border", BorderListener.BORDER_SIZE);

                FileUtils.write(mapInfo, Atheneum.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveNetherBuffer() {
        try {
            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                dbObject.put("netherBuffer", HCFactions.getInstance().getMapHandler().getNetherBuffer());

                FileUtils.write(mapInfo, Atheneum.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveWorldBuffer() {
        try {
            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                dbObject.put("worldBuffer", HCFactions.getInstance().getMapHandler().getWorldBuffer());

                FileUtils.write(mapInfo, Atheneum.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}