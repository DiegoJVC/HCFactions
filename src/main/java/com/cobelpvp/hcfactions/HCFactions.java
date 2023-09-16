package com.cobelpvp.hcfactions;

import com.cobelpvp.hcfactions.chat.ChatHandler;
import com.cobelpvp.hcfactions.events.EventHandler;
import com.cobelpvp.hcfactions.events.systemfactions.citadel.CitadelHandler;
import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.GlowHandler;
import com.cobelpvp.hcfactions.factions.FactionHandler;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.hcfactions.factions.dtr.DTRHandler;
import com.cobelpvp.hcfactions.listener.*;
import com.cobelpvp.hcfactions.map.MapHandler;
import com.cobelpvp.hcfactions.persist.RedisSaveTask;
import com.cobelpvp.hcfactions.persist.maps.*;
import com.cobelpvp.hcfactions.reclaims.ReclaimHandler;
import com.cobelpvp.hcfactions.server.EnderpearlCooldownHandler;
import com.cobelpvp.hcfactions.server.ServerHandler;
import com.cobelpvp.hcfactions.util.RegenUtils;
import com.cobelpvp.hcfactions.util.ServerFakeFreezeTask;
import com.cobelpvp.hcfactions.util.protocol.ClientCommandPacketAdaper;
import com.cobelpvp.hcfactions.util.protocol.SignGUIPacketAdaper;
import com.cobelpvp.hcfactions.crates.Crate;
import com.cobelpvp.hcfactions.crates.handlers.*;
import com.cobelpvp.hcfactions.crates.listeners.*;
import com.cobelpvp.hcfactions.crates.utils.Version_Util;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.io.ByteStreams;
import com.mongodb.MongoClient;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.command.TeamsCommandHandler;
import com.cobelpvp.hcfactions.deathmessage.DeathMessageHandler;
import com.cobelpvp.hcfactions.mobstack.AreaChecker;
import com.cobelpvp.hcfactions.mobstack.listener.MobListener;
import com.cobelpvp.hcfactions.mobstack.task.MergeTask;
import com.cobelpvp.hcfactions.pvpclass.PvPClassHandler;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdClaim;
import com.cobelpvp.hcfactions.factions.commands.faction.subclaim.TeamSubclaimCommand;
import com.cobelpvp.hcfactions.util.PacketBorderThread;
import com.cobelpvp.hcfactions.persist.maps.statistics.BaseStatisticMap;
import com.cobelpvp.hcfactions.persist.maps.statistics.EnderPearlsUsedMap;
import com.cobelpvp.hcfactions.persist.maps.statistics.ExpCollectedMap;
import com.cobelpvp.hcfactions.persist.maps.statistics.ItemsRepairedMap;
import com.cobelpvp.hcfactions.persist.maps.statistics.SplashPotionsBrewedMap;
import com.cobelpvp.hcfactions.persist.maps.statistics.SplashPotionsUsedMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HCFactions extends JavaPlugin {

	public static String MONGO_DB_NAME = "HCFactions";

	@Getter private static HCFactions instance;

	@Getter private MongoClient mongoPool;

	@Getter private ChatHandler chatHandler;
	@Getter private PvPClassHandler pvpClassHandler;
	@Getter private FactionHandler factionHandler;
	@Getter private ServerHandler serverHandler;
	@Getter private MapHandler mapHandler;
	@Getter private ReclaimMap reclaimMap;
	@Getter private CitadelHandler citadelHandler;
	@Getter private EventHandler eventHandler;
	@Getter private GlowHandler glowHandler;
	@Getter private String pluginPrefix;
	@Getter private PlaytimeMap playtimeMap;
	@Getter private OppleMap oppleMap;
	@Getter private DeathbanMap deathbanMap;
	@Getter private com.cobelpvp.hcfactions.persist.maps.PvPTimerMap PvPTimerMap;
	@Getter private StartingPvPTimerMap startingPvPTimerMap;
	@Getter private DeathsMap deathsMap;
	@Getter private KillsMap killsMap;
	@Getter private ChatModeMap chatModeMap;
	@Getter private ToggleGlobalChatMap toggleGlobalChatMap;
	@Getter private ChatSpyMap chatSpyMap;
	@Getter private DiamondMinedMap diamondMinedMap;
	@Getter private GoldMinedMap goldMinedMap;
	@Getter private IronMinedMap ironMinedMap;
	@Getter private CoalMinedMap coalMinedMap;
	@Getter private RedstoneMinedMap redstoneMinedMap;
	@Getter private LapisMinedMap lapisMinedMap;
	@Getter private EmeraldMinedMap emeraldMinedMap;
	@Getter private FirstJoinMap firstJoinMap;
	@Getter private LastJoinMap lastJoinMap;
	@Getter private LivesMap livesMap;
	@Getter private BaseStatisticMap enderPearlsUsedMap;
	@Getter private BaseStatisticMap expCollectedMap;
	@Getter private BaseStatisticMap itemsRepairedMap;
	@Getter private BaseStatisticMap splashPotionsBrewedMap;
	@Getter private BaseStatisticMap splashPotionsUsedMap;
	@Getter private WrappedBalanceMap wrappedBalanceMap;
	@Getter private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
	@Getter private IPMap ipMap;
	@Getter private WhitelistedIPMap whitelistedIPMap;
	@Getter private KDRMap kdrMap;
	@Getter private AreaChecker areaChecker;
	@Getter private ConfigHandler configHandler;
	@Getter private CrateHandler crateHandler;
	@Getter private MessageHandler messageHandler = new MessageHandler(this);
	@Getter private SettingsHandler settingsHandler;
	@Getter private Version_Util version_util;
	@Getter private OpenHandler openHandler;
	@Getter private File dataFile;
	@Getter private ReclaimHandler reclaimHandler;
	@Getter private YamlConfiguration dataConfig;
	@Getter private YamlConfiguration messagesConfig;
	@Getter private File messagesFile;
	@Getter private final String stackPrefix = ChatColor.BLUE + ChatColor.RED.toString() + ChatColor.YELLOW + "x";
	@Getter private int areaLimit = 200;
	@Getter private int breedingCooldown = 40;
	@Getter private int nameDistance = 10;

	@Getter private CombatLoggerListener combatLoggerListener;
	@Getter @Setter
	private Predicate<Player> inDuelPredicate = (player) -> false;
	private ArrayList<UUID> creatingCrate = new ArrayList<>();

	@Override
	public void onEnable() {
		if (Bukkit.getServerName().contains(" ")) {
			System.out.println("SET server-name VALUE IN server.properties TO");
			System.out.println("A PROPER SERVER NAME. THIS WILL BE USED AS THE");
			System.out.println("MONGO DATABASE NAME.");
			this.getServer().shutdown();
			return;
		}

		instance = this;
		saveDefaultConfig();

		areaChecker = chunk -> true;
		getServer().getPluginManager().registerEvents(new MobListener(this), this);
		MergeTask mergeTask = new MergeTask(this);
		mergeTask.runTaskTimer(this, 5, 5);

		try {
			mongoPool = new MongoClient(getConfig().getString("Mongo.Host", "127.0.0.1"));
			MONGO_DB_NAME = Bukkit.getServerName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		(new DTRHandler()).runTaskTimer(this, 20L, 1200L);
		(new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
		(new PacketBorderThread()).start();

		setupHandlers();
		setupPersistence();
		setupListeners();

		ProtocolLibrary.getProtocolManager().addPacketListener(new SignGUIPacketAdaper());
		ProtocolLibrary.getProtocolManager().addPacketListener(new ClientCommandPacketAdaper());

		for (World world : Bukkit.getWorlds()) {
			world.setThundering(false);
			world.setStorm(false);
			world.setWeatherDuration(Integer.MAX_VALUE);
			world.setGameRuleValue("doFireTick", "false");
			world.setGameRuleValue("mobGriefing", "false");
		}

		new ServerFakeFreezeTask().runTaskTimerAsynchronously(this, 20L, 20L);
		setupCrates();
	}

	@Override
	public void onDisable() {
		getEventHandler().saveEvents();

		for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
			getPlaytimeMap().playerQuit(player.getUniqueId(), false);
			player.setMetadata("loggedout", new FixedMetadataValue(this, true));
		}

		for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
			PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
		}

		for (Entity e : this.combatLoggerListener.getCombatLoggers()) {
			if (e != null) {
				e.remove();
			}
		}

		RedisSaveTask.save(null, false);

		getInstance().getServerHandler().save();

		RegenUtils.resetAll();

		Atheneum.getInstance().runRedisCommand((jedis) -> {
			jedis.save();
			return null;
		});

		if (configHandler != null) {
			for (Map.Entry<String, Crate> crate : configHandler.getCrates().entrySet()) {
			}
		}
	}

	private void setupHandlers() {
		serverHandler = new ServerHandler();
		mapHandler = new MapHandler();
		mapHandler.load();

		factionHandler = new FactionHandler();
		LandBoard.getInstance().loadFromTeams();

		chatHandler = new ChatHandler();
		citadelHandler = new CitadelHandler();
		pvpClassHandler = new PvPClassHandler();
		eventHandler = new EventHandler();
		reclaimHandler = new ReclaimHandler();

		if (getConfig().getBoolean("glowstoneMountain", false)) {
			glowHandler = new GlowHandler();
		}

		TeamsCommandHandler.registerAll(this);

		DeathMessageHandler.init();
	}

	private void setupListeners() {
		getServer().getPluginManager().registerEvents(new MapListener(), this);
		getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
		getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
		getServer().getPluginManager().registerEvents(new BorderListener(), this);
		getServer().getPluginManager().registerEvents((combatLoggerListener = new CombatLoggerListener()), this);
		getServer().getPluginManager().registerEvents(new CrowbarListener(), this);
		getServer().getPluginManager().registerEvents(new DeathbanListener(), this);
		getServer().getPluginManager().registerEvents(new EnchantmentLimiterListener(), this);
		getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), this);
		getServer().getPluginManager().registerEvents(new EndListener(), this);
		getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), this);
		getServer().getPluginManager().registerEvents(new HCFactionsListener(), this);
		getServer().getPluginManager().registerEvents(new GoldenAppleListener(), this);
		getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
		getServer().getPluginManager().registerEvents(new FastBowListener(), this);
		getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
		getServer().getPluginManager().registerEvents(new PotionLimiterListener(), this);
		getServer().getPluginManager().registerEvents(new NetherPortalListener(), this);
		getServer().getPluginManager().registerEvents(new ElevatorListener(), this);
		getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
		getServer().getPluginManager().registerEvents(new SignSubclaimListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), this);
		getServer().getPluginManager().registerEvents(new AntiKeyRenameListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnListener(), this);
		getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
		getServer().getPluginManager().registerEvents(new StaffUtilsListener(), this);
		getServer().getPluginManager().registerEvents(new TeamListener(), this);
		getServer().getPluginManager().registerEvents(new WebsiteListener(), this);
		getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), this);
		getServer().getPluginManager().registerEvents(new CmdClaim(), this);
		getServer().getPluginManager().registerEvents(new StatTrakListener(), this);

		if (getServerHandler().isBlockEntitiesThroughPortals()) {
			getServer().getPluginManager().registerEvents(new EntityPortalListener(), this);
		}

		if (getServerHandler().isBlockRemovalEnabled()) {
			getServer().getPluginManager().registerEvents(new BlockRegenListener(), this);
		}

		getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), this);
	}

	private void setupPersistence() {
		(playtimeMap = new PlaytimeMap()).loadFromRedis();
		(oppleMap = new OppleMap()).loadFromRedis();
		(deathbanMap = new DeathbanMap()).loadFromRedis();
		(PvPTimerMap = new PvPTimerMap()).loadFromRedis();
		(startingPvPTimerMap = new StartingPvPTimerMap()).loadFromRedis();
		(deathsMap = new DeathsMap()).loadFromRedis();
		(killsMap = new KillsMap()).loadFromRedis();
		(chatModeMap = new ChatModeMap()).loadFromRedis();
		(toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
		(livesMap = new LivesMap()).loadFromRedis();
		(chatSpyMap = new ChatSpyMap()).loadFromRedis();
		(diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
		(goldMinedMap = new GoldMinedMap()).loadFromRedis();
		(ironMinedMap = new IronMinedMap()).loadFromRedis();
		(coalMinedMap = new CoalMinedMap()).loadFromRedis();
		(redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
		(lapisMinedMap = new LapisMinedMap()).loadFromRedis();
		(emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
		(reclaimMap = new ReclaimMap()).loadFromRedis();
		(firstJoinMap = new FirstJoinMap()).loadFromRedis();
		(lastJoinMap = new LastJoinMap()).loadFromRedis();
		(enderPearlsUsedMap = new EnderPearlsUsedMap()).loadFromRedis();
		(expCollectedMap = new ExpCollectedMap()).loadFromRedis();
		(itemsRepairedMap = new ItemsRepairedMap()).loadFromRedis();
		(splashPotionsBrewedMap = new SplashPotionsBrewedMap()).loadFromRedis();
		(splashPotionsUsedMap = new SplashPotionsUsedMap()).loadFromRedis();
		(wrappedBalanceMap = new WrappedBalanceMap()).loadFromRedis();
		(toggleFoundDiamondsMap = new ToggleFoundDiamondsMap()).loadFromRedis();
		(ipMap = new IPMap()).loadFromRedis();
		(whitelistedIPMap = new WhitelistedIPMap()).loadFromRedis();
		(kdrMap = new KDRMap()).loadFromRedis();

		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("LIMITER.PROTECTION"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.DAMAGE_ALL, getConfig().getInt("LIMITER.SHARPNESS"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.ARROW_KNOCKBACK, getConfig().getInt("LIMITER.PUNCH"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.KNOCKBACK, getConfig().getInt("LIMITER.KNOCKBACK"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.FIRE_ASPECT, getConfig().getInt("LIMITER.FIRE_ASPECT"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.THORNS, getConfig().getInt("LIMITER.THORNS"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.ARROW_FIRE, getConfig().getInt("LIMITER.FLAME"));
		EnchantmentLimiterListener.ENCHANTMENT_LIMITS.put(Enchantment.ARROW_DAMAGE, getConfig().getInt("LIMITER.POWER"));
	}


	public boolean isStackable(LivingEntity entity) {
		switch (entity.getType()) {
			case ZOMBIE:
			case SKELETON:
			case SPIDER:
			case CAVE_SPIDER:
			case PIG_ZOMBIE:
				return true;
			case COW:
			case PIG:
			case CHICKEN:
				return ((Ageable) entity).isAdult();
			default:
				return false;
		}
	}

	public int getStackAmount(LivingEntity entity) {
		String name = entity.getCustomName();
		if (name == null) {
			return 1;
		}
		if (!name.startsWith(stackPrefix)) {
			return 1;
		}
		return Integer.parseInt(name.substring(stackPrefix.length()));
	}

	public void addToStack(LivingEntity entity, int add) {
		int amount = getStackAmount(entity);
		entity.setCustomName(stackPrefix + Integer.toString(amount + add));
		entity.setCanPickupItems(false);
		entity.getEquipment().setArmorContents(null);
		if (entity instanceof Zombie) {
			((Zombie) entity).setBaby(false);
		}
	}

	public void decrementStack(LivingEntity entity) {
		int amount = getStackAmount(entity);
		if (amount > 2) {
			entity.setCustomName(stackPrefix + Integer.toString(--amount));
		} else {
			entity.setCustomName(null);
			entity.setCustomNameVisible(true);
			entity.setCustomNameVisible(false);
		}
	}

	public String getName(LivingEntity entity) {
		switch (entity.getType()) {
			case ZOMBIE:
				return "Zombie";
			case SKELETON:
				return "Skeleton";
			case SPIDER:
				return "Spider";
			case CAVE_SPIDER:
				return "Cave Spider";
			case PIG_ZOMBIE:
				return "Pig Zombie";
			case COW:
				return "Cow";
			case PIG:
				return "Pig";
			case CHICKEN:
				return "Chicken";
			case SHEEP:
				return "Sheep";
			default:
				return entity.getType().name();
		}
	}

	public boolean canBreed(Ageable entity) {
		return entity.getAge() == 0 && getNearbyCount(entity) < areaLimit;
	}

	public void breedStack(Player player, Ageable entity) {
		entity.getWorld().spawn(entity.getLocation(), entity.getClass()).setBaby();
		entity.setAge(breedingCooldown);
		useUpItem(player, 2);
		entity.getWorld().spigot().playEffect(entity.getLocation().add(0, 1, 0), Effect.HAPPY_VILLAGER, 1, 1, 0.5f, 0.5f, 0.5f, 0.5f, 20, 16);
	}

	public void useUpItem(Player player, int amount) {
		ItemStack item = player.getItemInHand();
		if (item.getAmount() > amount) {
			item.setAmount(item.getAmount() - amount);
		} else {
			player.setItemInHand(null);
		}
	}

	public int getNearbyCount(LivingEntity entity) {
		int radius = 24;
		int count = getStackAmount(entity);
		for (Entity other : entity.getNearbyEntities(radius, 256, radius)) {
			if (other.getType() == entity.getType()) {
				count += getStackAmount((LivingEntity) other);
			}
		}
		return count;
	}

	public void setupCrates() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		dataFile = new File(getDataFolder(), "data.yml");
		dataConfig = YamlConfiguration.loadConfiguration(dataFile);
		try {
			dataConfig.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!dataConfig.isSet("Data Version") || dataConfig.getInt("Data Version") == 1) {
			dataConfig.set("Data Version", 2);
			if (dataConfig.isSet("Crate Locations"))
				dataConfig.set("Crate Locations", null);
			try {
				dataConfig.save(dataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		messagesFile = new File(getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			try {
				messagesFile.createNewFile();
				InputStream inputStream = getResource("messages.yml");
				OutputStream outputStream = new FileOutputStream(messagesFile);
				ByteStreams.copy(inputStream, outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

		if (!messagesConfig.isSet("Prefix"))
			messagesConfig.set("Prefix", "&7[&9Crates&7]");

		if (!messagesConfig.isSet("Command No Permission"))
			messagesConfig.set("Command No Permission", "&cYou do not have the correct permission to run this command");

		if (!messagesConfig.isSet("Crate No Permission"))
			messagesConfig.set("Crate No Permission", "&cYou do not have the correct permission to use this crate");

		if (!messagesConfig.isSet("Crate Open Without Key"))
			messagesConfig.set("Crate Open Without Key", "&cYou must be holding a %crate% &ckey to open this crate");

		if (!messagesConfig.isSet("Key Given"))
			messagesConfig.set("Key Given", "&aYou have been given a %crate% &acrate key");

		if (!messagesConfig.isSet("Broadcast"))
			messagesConfig.set("Broadcast", "&d%displayname% &dopened a %crate% &dcrate");

		if (!messagesConfig.isSet("Cant Place"))
			messagesConfig.set("Cant Place", "&cYou can not place crate keys");

		if (!messagesConfig.isSet("Cant Drop"))
			messagesConfig.set("Cant Drop", "&cYou can not drop crate keys");

		if (!messagesConfig.isSet("Chance Message"))
			messagesConfig.set("Chance Message", "&d%percentage%% Chance");

		if (!messagesConfig.isSet("Inventory Full Claim"))
			messagesConfig.set("Inventory Full Claim", "&aYou're inventory is full, you can claim your keys later using /claim");

		if (!messagesConfig.isSet("Claim Join"))
			messagesConfig.set("Claim Join", "&aYou currently have keys waiting to be claimed, use /crate to claim");

		if (!messagesConfig.isSet("Possible Wins Title"))
			messagesConfig.set("Possible Wins Title", "Possible Wins:");

		try {
			messagesConfig.save(messagesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (getConfig().isSet("Messages")) {
			for (String path : getConfig().getConfigurationSection("Messages").getKeys(false)) {
				messagesConfig.set(path, getConfig().getString("Messages." + path));
			}
			try {
				messagesConfig.save(messagesFile);
				getConfig().set("Messages", null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		pluginPrefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("Prefix")) + " " + ChatColor.RESET;

		version_util = new Version_Util(this);
		configHandler = new ConfigHandler(getConfig(), this);
		crateHandler = new CrateHandler(this);
		openHandler = new OpenHandler(this);
		settingsHandler = new SettingsHandler(this);

		TeamsCommandHandler.registerPackage(this, "com.cobelpvp.hcfactions.crates.commands");

		Bukkit.getPluginManager().registerEvents(new BlockListeners(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new InventoryInteract(this), this);
		Bukkit.getPluginManager().registerEvents(new SettingsListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteract(this), this);

		loadMetaData();
	}

	private void loadMetaData() {
		if (!dataConfig.isSet("Crate Locations"))
			return;
		for (String name : dataConfig.getConfigurationSection("Crate Locations").getKeys(false)) {
			final Crate crate = configHandler.getCrate(name.toLowerCase());
			if (crate == null)
				continue;
			String path = "Crate Locations." + name;
			List<String> locations = dataConfig.getStringList(path);

			for (String location : locations) {
				List<String> strings = Arrays.asList(location.split("\\|"));
				if (strings.size() < 4)
					continue;
				if (strings.size() > 4) {
					for (int i = 0; i < strings.size(); i++) {
						if (strings.get(i).isEmpty() || strings.get(i).equals("")) {
							strings.remove(i);
						}
					}
				}
				Location locationObj = new Location(Bukkit.getWorld(strings.get(0)), Double.parseDouble(strings.get(1)), Double.parseDouble(strings.get(2)), Double.parseDouble(strings.get(3)));
				Block block = locationObj.getBlock();
				if (block == null || block.getType().equals(Material.AIR)) {
					getLogger().warning("No block found at " + location + " removing from data.yml");
					crate.removeFromConfig(locationObj);
					continue;
				}
				Location location1 = locationObj.getBlock().getLocation().add(0.5, 0.5, 0.5);
				final HCFactions hcfactions = this;
				block.setMetadata("CrateType", new MetadataValue() {
					@Override
					public Object value() {
						return crate.getName(false);
					}

					@Override
					public int asInt() {
						return 0;
					}

					@Override
					public float asFloat() {
						return 0;
					}

					@Override
					public double asDouble() {
						return 0;
					}

					@Override
					public long asLong() {
						return 0;
					}

					@Override
					public short asShort() {
						return 0;
					}

					@Override
					public byte asByte() {
						return 0;
					}

					@Override
					public boolean asBoolean() {
						return false;
					}

					@Override
					public String asString() {
						return value().toString();
					}

					@Override
					public Plugin getOwningPlugin() {
						return hcfactions;
					}

					@Override
					public void invalidate() {

					}
				});
			}


		}
	}

	public boolean isCreating(UUID uuid) {
		return creatingCrate.contains(uuid);
	}

	public void addCreating(UUID uuid) {
		creatingCrate.add(uuid);
	}

	public void removeCreating(UUID uuid) {
		creatingCrate.remove(uuid);
	}
}
