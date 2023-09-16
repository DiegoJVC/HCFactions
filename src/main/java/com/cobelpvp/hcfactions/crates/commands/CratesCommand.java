package com.cobelpvp.hcfactions.crates.commands;

import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.IOException;
import java.util.Map;

public class CratesCommand {

    private static HCFactions hcfactions = HCFactions.getInstance();

    @Command(names = "crate create", permission = "hcfactions.crates.admin")
    public static void crateCreate(CommandSender sender, @Param(name = "crateName") String crateName) {
        String name = crateName;
        FileConfiguration config = HCFactions.getInstance().getConfig();

        if (config.isSet("Crates." + name)) {
            sender.sendMessage(translate(HCFactions.getInstance().getMessagesConfig().getString("Crate Exists").replace("{0}", crateName)));
            return;
        }

        config.set("Crates." + name + ".Winnings.1.Type", "ITEM");
        config.set("Crates." + name + ".Winnings.1.Item Type", "DIAMOND_SWORD");
        config.set("Crates." + name + ".Winnings.1.Item Data", 0);
        config.set("Crates." + name + ".Winnings.1.Percentage", 0);
        config.set("Crates." + name + ".Winnings.1.Name", "&6&lTest");
        config.set("Crates." + name + ".Winnings.1.Amount", 1);

        config.set("Crates." + name + ".Key.Item", "TRIPWIRE_HOOK");
        config.set("Crates." + name + ".Key.Name", "%type% Crate Key");
        config.set("Crates." + name + ".Key.Enchanted", true);

        config.set("Crates." + name + ".Knockback", 0.0);
        config.set("Crates." + name + ".Broadcast", false);
        config.set("Crates." + name + ".Firework", false);
        config.set("Crates." + name + ".Preview", true);
        config.set("Crates." + name + ".Block", "CHEST");
        config.set("Crates." + name + ".Color", "GREEN");
        HCFactions.getInstance().saveConfig();
        HCFactions.getInstance().reloadConfig();

        HCFactions.getInstance().getConfigHandler().getCrates().put(name.toLowerCase(), new Crate(name, HCFactions.getInstance(), HCFactions.getInstance().getConfigHandler()));
        HCFactions.getInstance().getSettingsHandler().setupCratesInventory();

        sender.sendMessage(translate(HCFactions.getInstance().getMessagesConfig().getString("Crate Created").replace("{0}", crateName)));
        sender.sendMessage(HCFactions.getInstance().getPluginPrefix() + ChatColor.GREEN + name + " crate has been created");
    }

    @Command(names = {"crate rename"}, permission = "hcfactions.crates.admin")
    public static void crateRename(CommandSender sender, @Param(name = "oldName") String oldName, @Param(name = "newName") String newName) {
        if (!hcfactions.getConfigHandler().getCrates().containsKey(oldName.toLowerCase())) {
            sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Crate Not Found").replace("{0}", oldName)));
            return;
        }

        Crate crate = hcfactions.getConfigHandler().getCrates().get(oldName.toLowerCase());

        FileConfiguration config = hcfactions.getConfig();
        if (config.isSet("Crates." + newName)) {
            sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Crate Exists").replace("{0}", newName)));
            return;
        }

        for (String id : hcfactions.getConfig().getConfigurationSection("Crates." + crate.getName(false) + ".Winnings").getKeys(false)) {
            String path = "Crates." + crate.getName(false) + ".Winnings." + id;
            String newPath = "Crates." + newName + ".Winnings." + id;

            if (config.isSet(path + ".Type"))
                config.set(newPath + ".Type", config.getString(path + ".Type"));
            if (config.isSet(path + ".Item Type"))
                config.set(newPath + ".Item Type", config.getString(path + ".Item Type"));
            if (config.isSet(path + ".Item Data"))
                config.set(newPath + ".Item Data", config.getInt(path + ".Item Data"));
            if (config.isSet(path + ".Percentage"))
                config.set(newPath + ".Percentage", config.getDouble(path + ".Percentage"));
            if (config.isSet(path + ".Name"))
                config.set(newPath + ".Name", config.getString(path + ".Name"));
            if (config.isSet(path + ".Amount"))
                config.set(newPath + ".Amount", config.getInt(path + ".Amount"));
            if (config.isSet(path + ".Enchantments"))
                config.set(newPath + ".Enchantments", config.getList(path + ".Enchantments"));
            if (config.isSet(path + ".Commands"))
                config.set(newPath + ".Commands", config.getList(path + ".Commands"));
        }

        config.set("Crates." + newName + ".Knockback", config.getDouble("Crates." + crate.getName(false) + ".Knockback"));
        if (config.isSet("Crates." + crate.getName(false) + ".Block"))
            config.set("Crates." + newName + ".Block", config.getString("Crates." + crate.getName(false) + ".Block"));
        if (config.isSet("Crates." + crate.getName(false) + ".Color"))
            config.set("Crates." + newName + ".Color", config.getString("Crates." + crate.getName(false) + ".Color"));
        if (config.isSet("Crates." + crate.getName(false) + ".Knockback"))
            config.set("Crates." + newName + ".Knockback", config.getDouble("Crates." + crate.getName(false) + ".Knockback"));
        if (config.isSet("Crates." + crate.getName(false) + ".Broadcast"))
            config.set("Crates." + newName + ".Broadcast", config.getBoolean("Crates." + crate.getName(false) + ".Broadcast"));
        if (config.isSet("Crates." + crate.getName(false) + ".Firework"))
            config.set("Crates." + newName + ".Firework", config.getBoolean("Crates." + crate.getName(false) + ".Firework"));
        if (config.isSet("Crates." + crate.getName(false) + ".Preview"))
            config.set("Crates." + newName + ".Preview", config.getBoolean("Crates." + crate.getName(false) + ".Preview"));
        if (config.isSet("Crates." + crate.getName(false) + ".Permission"))
            config.set("Crates." + newName + ".Permission", config.getString("Crates." + crate.getName(false) + ".Permission"));
        if (config.isSet("Crates." + crate.getName(false) + ".Hide Percentages"))
            config.set("Crates." + newName + ".Hide Percentages", config.getBoolean("Crates." + crate.getName(false) + ".Hide Percentages"));
        if (config.isSet("Crates." + crate.getName(false) + ".Opener"))
            config.set("Crates." + newName + ".Opener", config.getString("Crates." + crate.getName(false) + ".Opener"));
        if (config.isSet("Crates." + crate.getName(false) + ".Cooldown"))
            config.set("Crates." + newName + ".Cooldown", config.getInt("Crates." + crate.getName(false) + ".Cooldown"));

        // Clone the crate key
        if (config.isSet("Crates." + crate.getName(false) + ".Key.Item"))
            config.set("Crates." + newName + ".Key.Item", config.getString("Crates." + crate.getName(false) + ".Key.Item"));
        if (config.isSet("Crates." + crate.getName(false) + ".Key.Name"))
            config.set("Crates." + newName + ".Key.Name", config.getString("Crates." + crate.getName(false) + ".Key.Name"));
        if (config.isSet("Crates." + crate.getName(false) + ".Key.Enchanted"))
            config.set("Crates." + newName + ".Key.Enchanted", config.getBoolean("Crates." + crate.getName(false) + ".Key.Enchanted"));

        // Rename data fields
        FileConfiguration dataConfig = hcfactions.getDataConfig();
        if (dataConfig.isSet("Crate Locations." + crate.getName(false).toLowerCase())) {
            dataConfig.set("Crate Locations." + newName.toLowerCase(), dataConfig.getStringList("Crate Locations." + crate.getName(false).toLowerCase()));
            dataConfig.set("Crate Locations." + crate.getName(false).toLowerCase(), null);
            try {
                dataConfig.save(hcfactions.getDataFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config.set("Crates." + crate.getName(false), null);
        hcfactions.saveConfig();
        hcfactions.reloadConfig();

        hcfactions.getConfigHandler().getCrates().remove(oldName.toLowerCase());
        hcfactions.getConfigHandler().getCrates().put(newName.toLowerCase(), new Crate(newName, hcfactions, hcfactions.getConfigHandler()));
        hcfactions.getSettingsHandler().setupCratesInventory();

        sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Crate Renamed").replace("{0}", oldName).replace("{1}", newName)));
    }

    @Command(names = {"crate delete"}, permission = "hcfactions.crates.admin")
    public static void crateDelete(CommandSender sender, @Param(name = "crateName") String name) {
        FileConfiguration config = hcfactions.getConfig();
        if (!config.isSet("Crates." + name)) {
            sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Crate Not Found").replace("{0}", name)));
            return;
        }

        config.set("Crates." + name, null);
        hcfactions.saveConfig();
        hcfactions.reloadConfig();
        hcfactions.getConfigHandler().getCrates().remove(name.toLowerCase());
        hcfactions.getSettingsHandler().setupCratesInventory();

        sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Crate Deleted").replace("{0}", name)));
    }

    @Command(names = {"crate settings"}, permission = "hcfactions.crates.admin")
    public static void crateSettings(Player sender) {
        hcfactions.getSettingsHandler().openSettings(sender);
    }

    @Command(names = {"crate key"}, permission = "hcfactions.crates.admin")
    public static void crateKey(CommandSender sender, @Param(name = "keyName") String key, @Param(name = "amount") int amount, @Param(name = "player|all") String target) {
        OfflinePlayer offlinePlayer = null;
        if (!target.equalsIgnoreCase("all")) {
            offlinePlayer = Bukkit.getOfflinePlayer(target);
            if (offlinePlayer == null || (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())) { // Check if the player is online as "hasPlayedBefore" doesn't work until they disconnect?
                sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Player Not Found").replace("{0}", target)));
                return;
            }
        }

        String crateType = null;

        crateType = key;


        if (crateType != null) {
            if (hcfactions.getConfigHandler().getCrates().get(crateType.toLowerCase()) == null) {
                sender.sendMessage(hcfactions.getMessagesConfig().getString("Crate Not Found").replace("{0}", key));
                return;
            }

            if (offlinePlayer == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    hcfactions.getCrateHandler().giveCrateKey(p, crateType, amount);
                }
            } else {
                hcfactions.getCrateHandler().giveCrateKey(offlinePlayer, crateType, amount);
            }
        } else {
            if (offlinePlayer == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    hcfactions.getCrateHandler().giveCrateKey(p);
                }
            } else {
                hcfactions.getCrateHandler().giveCrateKey(offlinePlayer);
            }
        }

        if (offlinePlayer == null) {
            sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Given All")));
        } else {
            sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Given To").replace("{0}", offlinePlayer.getName())));
        }
    }

    @Command(names = {"crate crate"}, permission = "hcfactions.crates.admin")
    public static void crateCrate(Player sender, @Param(name = "crateName") String crateName) {
        String crateType;
        try {
            crateType = crateName;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(hcfactions.getMessagesConfig().getString("Crate Not Found").replace("{0}", crateName));
            return;
        }

        if (hcfactions.getConfigHandler().getCrates().get(crateType.toLowerCase()) == null) {
            sender.sendMessage(hcfactions.getMessagesConfig().getString("Crate Not Found").replace("{0}", crateName));
            return;
        }

        hcfactions.getCrateHandler().giveCrate(sender, crateType);


        sender.sendMessage(translate(hcfactions.getMessagesConfig().getString("Given Crate To")));
    }

    @Command(names = "crate claim", permission = "")
    public static void crateClaim(Player sender) {
        doClaim(sender);
    }

    private static void doClaim(Player player) {
        if (!hcfactions.getCrateHandler().hasPendingKeys(player.getUniqueId())) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You currently don't have any keys to claim");
            return;
        }
        Integer size = hcfactions.getCrateHandler().getPendingKey(player.getUniqueId()).size();
        if (size < 9)
            size = 9;
        else if (size <= 18)
            size = 18;
        else if (size <= 27)
            size = 27;
        else if (size <= 36)
            size = 36;
        else if (size <= 45)
            size = 45;
        else
            size = 54;
        Inventory inventory = Bukkit.createInventory(null, size, "Claim Crate Keys");
        Integer i = 0;
        for (Map.Entry<String, Integer> map : hcfactions.getCrateHandler().getPendingKey(player.getUniqueId()).entrySet()) {
            String crateName = map.getKey();
            Crate crate = hcfactions.getConfigHandler().getCrates().get(crateName.toLowerCase());
            if (crate == null)
                return; // Crate must have been removed?
            ItemStack keyItem = crate.getKey().getKeyItem(1);
            if (map.getValue() > 1) {
                ItemMeta itemMeta = keyItem.getItemMeta();
                itemMeta.setDisplayName(itemMeta.getDisplayName() + " x" + map.getValue());
                keyItem.setItemMeta(itemMeta);
            }
            inventory.setItem(i, keyItem);
            i++;
        }
        player.openInventory(inventory);
    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
