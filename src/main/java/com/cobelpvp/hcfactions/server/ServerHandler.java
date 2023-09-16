package com.cobelpvp.hcfactions.server;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHCFClaim;
import com.cobelpvp.atheneum.economy.TeamsEconomyHandler;
import com.cobelpvp.atheneum.util.ItemUtils;
import com.cobelpvp.hcfactions.server.uhc.UHCListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.util.InventoryUtils;
import com.cobelpvp.hcfactions.util.Logout;

public class ServerHandler {

    public static int WARZONE_RADIUS = 800;
    public static int WARZONE_BORDER = 2500;

    private final Map<PotionType, PotionStatus> potionStatus = new HashMap<>();

    @Getter private static final Map<String, Logout> tasks = new HashMap<>();

    @Getter private final String serverName;
    @Getter private final String networkWebsite;
    @Getter private final String tabServerName;
    @Getter private final String tabSectionColor;
    @Getter private final String tabInfoColor;

    @Getter private final boolean idleCheckEnabled;
    @Getter private final boolean startingTimerEnabled;
    @Getter private final boolean passiveTagEnabled;
    @Getter private final boolean waterPlacementInClaimsAllowed;
    @Getter private final boolean blockRemovalEnabled;
    @Getter private final boolean uhcHealing;

    @Getter private final boolean teamHQInEnemyClaims;

    @Getter private static final Map<String, Long> homeTimer = new ConcurrentHashMap<>();

    @Getter @Setter private boolean EOTW = false;
    @Getter @Setter private boolean PreEOTW = false;

    @Getter private final boolean blockEntitiesThroughPortals;

    @Getter private final ChatColor archerTagColor;
    @Getter private final ChatColor stunTagColor;
    @Getter private final ChatColor defaultRelationColor;

    @Getter private final boolean Cobel;

    @Getter private final boolean hardcore;
    @Getter private final boolean placeBlocksInCombat;

    public ServerHandler() {
        serverName = HCFactions.getInstance().getConfig().getString("serverName");
        networkWebsite = HCFactions.getInstance().getConfig().getString("networkWebsite");

        tabServerName = HCFactions.getInstance().getConfig().getString("tab.serverName");
        tabSectionColor = HCFactions.getInstance().getConfig().getString("tab.sectionColor");
        tabInfoColor = HCFactions.getInstance().getConfig().getString("tab.infoColor");

        idleCheckEnabled = HCFactions.getInstance().getConfig().getBoolean("idleCheck");
        startingTimerEnabled = HCFactions.getInstance().getConfig().getBoolean("startingTimer");
        passiveTagEnabled = HCFactions.getInstance().getConfig().getBoolean("passiveTag");
        uhcHealing = HCFactions.getInstance().getConfig().getBoolean("uhcHealing");
        waterPlacementInClaimsAllowed = HCFactions.getInstance().getConfig().getBoolean("waterPlacementInClaims");
        blockRemovalEnabled = HCFactions.getInstance().getConfig().getBoolean("blockRemoval");

        teamHQInEnemyClaims = HCFactions.getInstance().getConfig().getBoolean("teamHQInEnemyClaims", true);

        for (PotionType type : PotionType.values()) {
            if (type == PotionType.WATER) {
                continue;
            }

            PotionStatus status = new PotionStatus(HCFactions.getInstance().getConfig().getBoolean("potions." + type + ".drinkables"), HCFactions.getInstance().getConfig().getBoolean("potions." + type + ".splash"), HCFactions.getInstance().getConfig().getInt("potions." + type + ".maxLevel", -1));
            potionStatus.put(type, status);
        }

        if (uhcHealing) {
            Bukkit.getPluginManager().registerEvents(new UHCListener(), HCFactions.getInstance());
        }

        this.blockEntitiesThroughPortals = HCFactions.getInstance().getConfig().getBoolean("blockEntitiesThroughPortals", true);

        this.archerTagColor = ChatColor.valueOf(HCFactions.getInstance().getConfig().getString("archerTagColor", "RED"));
        this.stunTagColor = ChatColor.valueOf(HCFactions.getInstance().getConfig().getString("stunTagColor", "BLUE"));
        this.defaultRelationColor = ChatColor.valueOf(HCFactions.getInstance().getConfig().getString("defaultRelationColor", "RED"));

        this.Cobel = HCFactions.getInstance().getConfig().getBoolean("Cobel", false);
        if (this.Cobel) {
            Bukkit.getLogger().info("Cobel mode enabled!");
        }

        this.hardcore = HCFactions.getInstance().getConfig().getBoolean("hardcore", false);
        
        this.placeBlocksInCombat = HCFactions.getInstance().getConfig().getBoolean("placeBlocksInCombat", true);
        
        registerPlayerDamageRestrictionListener();
    }

    public void save() {
    }

    public String getEnchants() {
        if (Enchantment.DAMAGE_ALL.getMaxLevel() == 0 && Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0) {
            return "No Enchants";
        } else {
            return "Prot " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + ", Sharp " + Enchantment.DAMAGE_ALL.getMaxLevel();
        }
    }

    public boolean isWarzone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
            return (false);
        }

        return (Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS) || ((Math.abs(loc.getBlockX()) > WARZONE_BORDER || Math.abs(loc.getBlockZ()) > WARZONE_BORDER));
    }

    public boolean isSplashPotionAllowed(PotionType type) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).splash);
    }

    public boolean isDrinkablePotionAllowed(PotionType type) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).drinkables);
    }

    public boolean isPotionLevelAllowed(PotionType type, int amplifier) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).maxLevel == -1 || potionStatus.get(type).maxLevel >= amplifier);
    }

    public void startLogoutSequence(final Player player) {
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Logging out... " +ChatColor.YELLOW + "Please wait" + ChatColor.RED+ " 30" + ChatColor.YELLOW + " seconds.");

        BukkitTask taskid = new BukkitRunnable() {

            int seconds = 30;

            @Override
            public void run() {
                if (player.hasMetadata("frozen")) {
                    player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
                    cancel();
                    return;
                }

                seconds--;

                if (seconds == 0) {
                    if (tasks.containsKey(player.getName())) {
                        tasks.remove(player.getName());
                        player.setMetadata("loggedout", new FixedMetadataValue(HCFactions.getInstance(), true));
                        player.kickPlayer("§cYou have been safely logged out of the server!");
                        cancel();
                    }
                }

            }
        }.runTaskTimer(HCFactions.getInstance(), 20L, 20L);

        tasks.put(player.getName(), new Logout(taskid.getTaskId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)));
    }

    public RegionData getRegion(Faction ownerTo, Location location) {
        if (ownerTo != null && ownerTo.getOwner() == null) {
            if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE)) {
                return (new RegionData(RegionType.SPAWN, ownerTo));
            } else if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.KOTH)) {
                return (new RegionData(RegionType.KOTH, ownerTo));
            } else if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.CITADEL)) {
                return (new RegionData(RegionType.CITADEL, ownerTo));
            } else if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.ROAD)) {
                return (new RegionData(RegionType.ROAD, ownerTo));
            }
        }

        if (ownerTo != null) {
            return (new RegionData(RegionType.CLAIMED_LAND, ownerTo));
        } else if (isWarzone(location)) {
            return (new RegionData(RegionType.WARZONE, null));
        }

        return (new RegionData(RegionType.WILDNERNESS, null));
    }

    public boolean isUnclaimed(Location loc) {
        return (LandBoard.getInstance().getClaim(loc) == null && !isWarzone(loc));
    }

    public boolean isAdminOverride(Player player) {
        return (player.getGameMode() == GameMode.CREATIVE);
    }

    public Location getSpawnLocation() {
        return (HCFactions.getInstance().getServer().getWorld("world").getSpawnLocation().add(new Vector(0.5, 1, 0.5)));
    }

    public boolean isUnclaimedOrRaidable(Location loc) {
        Faction owner = LandBoard.getInstance().getTeam(loc);
        return (owner == null || owner.isRaidable());
    }

    public double getDTRLoss(Player player) {
        return (getDTRLoss(player.getLocation()));
    }

    public double getDTRLoss(Location location) {
        double dtrLoss = 1.00D;

        Faction ownerTo = LandBoard.getInstance().getTeam(location);

        if (HCFactions.getInstance().getConfig().getBoolean("oldhcf")) {
            if (location.getWorld().getEnvironment() == Environment.THE_END) {
                dtrLoss = 0.75D;
            } else if (location.getWorld().getEnvironment() == Environment.NETHER) {
                dtrLoss = 0.75D;
            }
        }

        if (ownerTo != null) {
            if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.QUARTER_DTR_LOSS)) {
                dtrLoss = 0.25D;
            } else if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.REDUCED_DTR_LOSS)) {
                dtrLoss = 0.75D;
            }
        }

        return (dtrLoss);
    }

    public long getDeathban(Player player) {
        return (getDeathban(player.getUniqueId(), player.getLocation()));
    }


    public long getDeathban(UUID playerUUID, Location location) {
        if (isPreEOTW()) {
            return (TimeUnit.DAYS.toSeconds(1000));
        }

        Faction ownerTo = LandBoard.getInstance().getTeam(location);
        Player player = HCFactions.getInstance().getServer().getPlayer(playerUUID);

        if (ownerTo != null && ownerTo.getOwner() == null) {
            Event linkedKOTH = HCFactions.getInstance().getEventHandler().getEvent(ownerTo.getName());

            if (linkedKOTH == null || linkedKOTH.isActive()) {
                if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.FIVE_MINUTE_DEATHBAN)) {
                    return (TimeUnit.MINUTES.toSeconds(5));
                } else if (ownerTo.hasDTRHCFClaim(DTRHCFClaim.FIFTEEN_MINUTE_DEATHBAN)) {
                    return (TimeUnit.MINUTES.toSeconds(15));
                }
            }
        }

        int max = Deathban.getDeathbanSeconds(player);

        long ban = HCFactions.getInstance().getPlaytimeMap().getPlaytime(playerUUID);

        if (player != null && HCFactions.getInstance().getPlaytimeMap().hasPlayed(playerUUID)) {
            ban += HCFactions.getInstance().getPlaytimeMap().getCurrentSession(playerUUID) / 1000L;
        }

        return (Math.min(max, ban));
    }

    public void beginHQWarp(final Player player, final Faction faction, int warmup, boolean charge) {
        Faction inClaim = LandBoard.getInstance().getTeam(player.getLocation());

        if(faction.getBalance() < 0) {
            faction.setBalance(0);
        }

        if (inClaim != null) {
            if (HCFactions.getInstance().getServerHandler().isHardcore() && inClaim.getOwner() != null && !inClaim.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You may not go to your faction headquarters from an enemy's faction! Use '/faction stuck' first.");
                return;
            }

            if (inClaim.getOwner() == null && (inClaim.hasDTRHCFClaim(DTRHCFClaim.KOTH) || inClaim.hasDTRHCFClaim(DTRHCFClaim.CITADEL))) {
                player.sendMessage(ChatColor.RED + "You may not go to your faction headquarters from inside of events!");
                return;
            }

            if (inClaim.hasDTRHCFClaim(DTRHCFClaim.SAFE_ZONE)) {
                if (player.getWorld().getEnvironment() != Environment.THE_END) {
                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + "'s Home.");
                    player.teleport(faction.getHQ());
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot teleport to your end headquarters while you're in end spawn!");
                }
                return;
            }
        }


        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not go to your faction headquarters while spawn tagged!");
            return;
        }

        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Your PvP Timer will be removed if the teleport is not cancelled.");
        }

        homeTimer.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmup));

        final int finalWarmup = warmup;

        new BukkitRunnable() {

            int time = finalWarmup;
            Location startLocation = player.getLocation();
            double startHealth = player.getHealth();

            @Override
            public void run() {
                time--;

                player.sendMessage(ChatColor.GOLD + "Teleporting to home in " + ChatColor.YELLOW + time + ChatColor.GOLD + " seconds.");

                if (!player.getLocation().getWorld().equals(startLocation.getWorld()) || player.getLocation().distanceSquared(startLocation) >= 0.1 || player.getHealth() < startHealth) {
                    player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
                    homeTimer.remove(player.getName());
                    cancel();
                    return;
                }

                startHealth = player.getHealth();

                if (homeTimer.containsKey(player.getName()) && homeTimer.get(player.getName()) <= System.currentTimeMillis()) {
                    if (HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        HCFactions.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                    }

                    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                        if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                            enderPearl.remove();
                        }
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + faction.getName() + ChatColor.YELLOW + "'s Home.");
                    player.teleport(faction.getHQ());
                    homeTimer.remove(player.getName());
                    cancel();
                    return;
                }

                if (time == 0) {
                        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        HCFactions.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                    }

                    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                        if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                            enderPearl.remove();
                        }
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.RED + faction.getName() + ChatColor.YELLOW + "'s Home.");
                    player.teleport(faction.getHQ());
                    homeTimer.remove(player.getName());
                    cancel();
                }
            }

        }.runTaskTimer(HCFactions.getInstance(), 20L, 20L);
    }

    private Map<UUID, Long> playerDamageRestrictMap = Maps.newHashMap();

    public void disablePlayerAttacking(final Player player, int seconds) {
        if (seconds == 10) {
            player.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
        }

        playerDamageRestrictMap.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
    }

    private void registerPlayerDamageRestrictionListener() {
    		HCFactions.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
    			@EventHandler(ignoreCancelled = true)
    			public void onDamage(EntityDamageByEntityEvent event) {
    				Long expiry = playerDamageRestrictMap.get(event.getDamager().getUniqueId());
    				if (expiry != null && System.currentTimeMillis() < expiry) {
    					event.setCancelled(true);
    				}
    			}

    			@EventHandler
    			public void onQuit(PlayerQuitEvent event) {
    				playerDamageRestrictMap.remove(event.getPlayer().getUniqueId());
    			}
    		}, HCFactions.getInstance());
    }

    public boolean isSpawnBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL){
            return (false);
        }

        int radius = HCFactions.getInstance().getMapHandler().getWorldBuffer();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public boolean isNetherBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NETHER){
            return (false);
        }

        int radius = HCFactions.getInstance().getMapHandler().getNetherBuffer();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public void handleShopSign(Sign sign, Player player) {
        ItemStack itemStack = (sign.getLine(2).contains("Crowbar") ? InventoryUtils.CROWBAR : ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", "")));

        if (itemStack == null && sign.getLine(2).contains("Skeleton")) {
            itemStack = InventoryUtils.SKELETONSPAWNER;
        }

        if (itemStack == null && sign.getLine(2).contains("Spider")) {
            itemStack = InventoryUtils.SPIDERSPAWNER;
        }

        if (itemStack == null) {
            System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
            return;
        }

        if (sign.getLine(0).toLowerCase().contains("buy")) {
            int price;
            int amount;

            try {
                price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));
            } catch (NumberFormatException e) {
                return;
            }

            if (TeamsEconomyHandler.getBalance(player.getUniqueId()) >= price) {

                if (TeamsEconomyHandler.getBalance(player.getUniqueId()) > 1000000) {
                    player.sendMessage("§cYour balance is too high. Please contact an admin to do this.");
                    Bukkit.getLogger().severe("[ECONOMY] " + player.getName() + " tried to buy shit at spawn with over 1Million." );
                    return;
                }


                if (Double.isNaN(TeamsEconomyHandler.getBalance(player.getUniqueId()))) {
                    TeamsEconomyHandler.setBalance(player.getUniqueId(), 0);
                    player.sendMessage("§cYour balance was broken.");
                    return;
                }

                if (player.getInventory().firstEmpty() != -1) {
                    TeamsEconomyHandler.withdraw(player.getUniqueId(), price);

                    itemStack.setAmount(amount);
                    player.getInventory().addItem(itemStack);
                    player.updateInventory();

                    showSignPacket(player, sign,
                            "§aBOUGHT§r " + amount,
                            "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(price),
                            "New Balance:",
                            "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) TeamsEconomyHandler.getBalance(player.getUniqueId()))
                    );
                } else {
                    showSignPacket(player, sign,
                            "§c§lError!",
                            "",
                            "§cNo space",
                            "§cin inventory!"
                    );
                }
            } else {
                showSignPacket(player, sign,
                        "§cInsufficient",
                        "§cfunds for",
                        sign.getLine(2),
                        sign.getLine(3)
                );
            }
        } else if (sign.getLine(0).toLowerCase().contains("sell")) {
            double pricePerItem;
            int amount;

            try {
                int price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));

                pricePerItem = (float) price / (float) amount;
            } catch (NumberFormatException e) {
                return;
            }

            int amountInInventory = Math.min(amount, countItems(player, itemStack.getType(), itemStack.getDurability()));

            if (amountInInventory == 0) {
                showSignPacket(player, sign,
                        "§cYou do not",
                        "§chave any",
                        sign.getLine(2),
                        "§con you!"
                );
            } else {
                int totalPrice = (int) (amountInInventory * pricePerItem);

                removeItem(player, itemStack, amountInInventory);
                player.updateInventory();

                TeamsEconomyHandler.deposit(player.getUniqueId(), totalPrice);

                showSignPacket(player, sign,
                        "§aSOLD§r " + amountInInventory,
                        "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice),
                        "New Balance:",
                        "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) TeamsEconomyHandler.getBalance(player.getUniqueId()))
                );
            }
        }
    }

    public void handleKitSign(Sign sign, Player player) {
        String kit = ChatColor.stripColor(sign.getLine(1));
    }

    public void removeItem(Player p, ItemStack it, int amount) {
        boolean specialDamage = it.getType().getMaxDurability() == (short) 0;

        for (int a = 0; a < amount; a++) {
            for (ItemStack i : p.getInventory()) {
                if (i != null) {
                    if (i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
                        if (i.getAmount() == 1) {
                            p.getInventory().clear(p.getInventory().first(i));
                            break;
                        } else {
                            i.setAmount(i.getAmount() - 1);
                            break;
                        }
                    }
                }
            }
        }

    }

    public ItemStack generateDeathSign(String killed, String killer) {
        ItemStack deathsign = new ItemStack(Material.SIGN);
        ItemMeta meta = deathsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§4" + killed);
        lore.add("§eSlain By:");
        lore.add("§a" + killer);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§dDeath Sign");
        deathsign.setItemMeta(meta);

        return (deathsign);
    }

    public ItemStack generateKOTHSign(String koth, String capper, EventType eventType) {
        ItemStack kothsign = new ItemStack(Material.SIGN);
        ItemMeta meta = kothsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§9" + koth);
        lore.add("§eCaptured By:");
        lore.add("§a" + capper);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§d" + eventType.name() + "Capture Sign");
        kothsign.setItemMeta(meta);

        return (kothsign);
    }

    private HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

    public void showSignPacket(Player player, final Sign sign, String... lines) {
        player.sendSignChange(sign.getLocation(), lines);

        if (showSignTasks.containsKey(sign)) {
            showSignTasks.remove(sign).cancel();
        }

        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run(){
                sign.update();
                showSignTasks.remove(sign);
            }

        };

        showSignTasks.put(sign, br);
        br.runTaskLater(HCFactions.getInstance(), 90L);
    }

    public int countItems(Player player, Material material, int damageValue) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int amount = 0;

        for (ItemStack item : items) {
            if (item != null) {
                boolean specialDamage = material.getMaxDurability() == (short) 0;

                if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short) damageValue)) {
                    amount += item.getAmount();
                }
            }
        }

        return (amount);
    }

    @AllArgsConstructor
    private static class PotionStatus {

        private final boolean drinkables;
        private final boolean splash;
        private final int maxLevel;

    }

}
