package com.cobelpvp.hcfactions.listener;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.listener.events.PrepareAnvilRepairEvent;
import com.cobelpvp.hcfactions.util.InventoryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EnchantmentLimiterListener implements Listener {

    public static final ImmutableSet<Character> ITEM_NAME_CHARACTER_BLACKLIST = ImmutableSet.of(
            '卍'
    );
    private final ImmutableMap<Material, EnumToolMaterial> ITEM_TOOL_MAPPING = ImmutableMap.of(Material.IRON_INGOT,
            EnumToolMaterial.IRON, Material.GOLD_INGOT, EnumToolMaterial.GOLD, Material.DIAMOND,
            EnumToolMaterial.DIAMOND);
    private final ImmutableMap<Material, EnumArmorMaterial> ITEM_ARMOUR_MAPPING = ImmutableMap.of(Material.IRON_INGOT,
            EnumArmorMaterial.IRON, Material.GOLD_INGOT, EnumArmorMaterial.GOLD, Material.DIAMOND,
            EnumArmorMaterial.DIAMOND);

    private Map<String, Long> lastArmorCheck = new HashMap<>();
    private Map<String, Long> lastSwordCheck = new HashMap<>();

    public static final Map<Enchantment, Integer> ENCHANTMENT_LIMITS = new HashMap<Enchantment, Integer>();

    private int getMaxLevel(Enchantment enchant) {
        return ENCHANTMENT_LIMITS.getOrDefault(enchant, enchant.getMaxLevel());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchantItem(EnchantItemEvent event) {
        Map<Enchantment, Integer> adding = event.getEnchantsToAdd();
        Iterator<Map.Entry<Enchantment, Integer>> iterator = adding.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Enchantment, Integer> entry = iterator.next();
            Enchantment enchantment = entry.getKey();
            int maxLevel = getMaxLevel(enchantment);
            if (entry.getValue() > maxLevel) {
                if (maxLevel > 0) {
                    adding.put(enchantment, maxLevel);
                } else {
                    iterator.remove();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFishEvent(PlayerFishEvent event) {
        Entity caught = event.getCaught();
        if ((caught instanceof org.bukkit.entity.Item)) {
            validateIllegalEnchants(((org.bukkit.entity.Item) caught).getItemStack());
        }
    }

    @EventHandler
    public void asd(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            InventoryView view = event.getView();
            ItemStack zero = view.getItem(0);
            ItemStack one = view.getItem(1);
            ItemStack result = view.getItem(2);

            if (zero == null || one == null || result == null) {
                return;
            }

            PrepareAnvilRepairEvent prepareAnvilRepairEvent = new PrepareAnvilRepairEvent(event.getWhoClicked(), event.getView(), event.getWhoClicked().getLocation().getBlock(), zero, one, result);
            Bukkit.getPluginManager().callEvent(prepareAnvilRepairEvent);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareAnvilRepair(PrepareAnvilRepairEvent event) {
        ItemStack firstAssassinEffects = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);
        if ((firstAssassinEffects != null) && (firstAssassinEffects.getType() != Material.AIR) && (second != null)
                && (second.getType() != Material.AIR)) {
            Object firstItemObj = net.minecraft.server.v1_7_R4.Item.REGISTRY.a(firstAssassinEffects.getTypeId());
            if ((firstItemObj instanceof net.minecraft.server.v1_7_R4.Item)) {
                net.minecraft.server.v1_7_R4.Item nmsFirstItem = (net.minecraft.server.v1_7_R4.Item) firstItemObj;
                if ((nmsFirstItem instanceof ItemTool)) {
                    if (this.ITEM_TOOL_MAPPING.get(second.getType()) != ((ItemTool) nmsFirstItem).i()) {
                    }
                } else if ((nmsFirstItem instanceof ItemSword)) {
                    EnumToolMaterial comparison = this.ITEM_TOOL_MAPPING.get(second.getType());
                    if ((comparison == null) || (comparison.e() != nmsFirstItem.c())) {
                    }
                } else if (((nmsFirstItem instanceof ItemArmor))
                        && (this.ITEM_ARMOUR_MAPPING.get(second.getType()) == ((ItemArmor) nmsFirstItem).m_())) {
                    return;
                }
            }
        }
        HumanEntity repairer = event.getRepairer();
        if ((repairer instanceof Player)) {
            validateIllegalEnchants(event.getResult());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean validateIllegalEnchants(ItemStack stack) {
        boolean updated = false;
        if ((stack != null) && (stack.getType() != Material.AIR)) {
            ItemMeta meta = stack.getItemMeta();
            Map.Entry<Enchantment, Integer> entry;
            if ((meta instanceof EnchantmentStorageMeta)) {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                Set<Map.Entry<Enchantment, Integer>> entries = enchantmentStorageMeta.getStoredEnchants().entrySet();
                for (Iterator localIterator = entries.iterator(); localIterator.hasNext();) {
                    entry = (Map.Entry) localIterator.next();
                    Enchantment enchantment = entry.getKey();
                    int maxLevel = getMaxLevel(enchantment);
                    if (((Integer) entry.getValue()).intValue() > maxLevel) {
                        updated = true;
                        if (maxLevel > 0) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, maxLevel, false);
                        } else {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                    }
                }
                stack.setItemMeta(meta);
            } else {
                Set<Map.Entry<Enchantment, Integer>> entries2 = stack.getEnchantments().entrySet();
                for (Map.Entry<Enchantment, Integer> entry2 : entries2) {
                    Enchantment enchantment2 = entry2.getKey();
                    int maxLevel2 = getMaxLevel(enchantment2);
                    if (entry2.getValue().intValue() > maxLevel2) {
                        updated = true;
                        stack.removeEnchantment(enchantment2);
                        if (maxLevel2 > 0) {
                            stack.addEnchantment(enchantment2, maxLevel2);
                        }
                    }
                }
            }
        }
        return updated;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack items = ((Player) event.getEntity()).getInventory().getItemInHand();
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
            boolean fixed = false;

            if (fixed) {
                ((Player) event.getEntity()).sendMessage(ChatColor.YELLOW + "We detected that your stuff had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack items = ((Player) event.getEntity()).getInventory().getItemInHand();
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
            boolean fixed = false;

            if (fixed) {
                ((Player) event.getEntity()).sendMessage(ChatColor.YELLOW + "We detected that your stuff had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null && event.getItem().getType() == Material.BOW) {
            ItemStack hand = event.getPlayer().getItemInHand();

            if (InventoryUtils.conformEnchants(hand)) {
                event.getPlayer().setItemInHand(hand);
                event.getPlayer().sendMessage(ChatColor.YELLOW + "We detected that your bow had some illegal enchantments, and have reduced the invalid enchantments.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof MerchantInventory) {
            for (ItemStack item : event.getInventory()) {
                if (item != null) {
                    InventoryUtils.conformEnchants(item);
                }
            }
        } else if (event.getInventory() instanceof AnvilInventory) {
            InventoryView view = event.getView();

            if (event.getCurrentItem() == null || event.getRawSlot() != view.convertSlot(event.getRawSlot()) || event.getRawSlot() != 2) {
                return;
            }

            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                ItemStack previous = event.getInventory().getItem(0);

                if (previous != null && previous.hasItemMeta() && previous.getItemMeta().hasDisplayName() && containsColor(previous.getItemMeta().getDisplayName())) {
                    event.setCancelled(false);
                    event.setResult(Event.Result.DENY);

                    view.close();

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ((Player) event.getWhoClicked()).giveExp(5);
                        }

                    }.runTaskLater(HCFactions.getInstance(), 2L);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ((Player) event.getWhoClicked()).giveExp(-5);
                        }

                    }.runTaskLater(HCFactions.getInstance(), 6L);
                    return;
                } else {
                    meta.setDisplayName(fixName(meta.getDisplayName()));
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
            }
        }
    }

    private boolean containsColor(String displayName) {
        return !ChatColor.stripColor(displayName).equals(displayName);
    }

    private String fixName(String name) {
        StringBuilder result = new StringBuilder();

        for (char nameCharacter : name.toCharArray()) {
            boolean blacklisted = false;

            for (char blacklistCharacter : ITEM_NAME_CHARACTER_BLACKLIST) {
                if (nameCharacter == blacklistCharacter) {
                    blacklisted = true;
                    break;
                }
            }

            if (!blacklisted) {
                result.append(nameCharacter);
            }
        }

        return (result.toString());
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        for (ItemStack drop : event.getDrops()) {
            InventoryUtils.conformEnchants(drop);
        }
    }

    @EventHandler
    public void onPlayerFishEvent2(PlayerFishEvent event) {
        if (event.getCaught() instanceof Item) {
            InventoryUtils.conformEnchants(((Item) event.getCaught()).getItemStack());
        }
    }

    public boolean checkArmor(Player player) {
        boolean check = !lastArmorCheck.containsKey(player.getName()) || (System.currentTimeMillis() - lastArmorCheck.get(player.getName())) > 5000L;

        if (check) {
            lastArmorCheck.put(player.getName(), System.currentTimeMillis());
        }

        return (check);
    }

    public boolean checkSword(Player player) {
        boolean check = !lastSwordCheck.containsKey(player.getName()) || (System.currentTimeMillis() - lastSwordCheck.get(player.getName())) > 5000L;

        if (check) {
            lastSwordCheck.put(player.getName(), System.currentTimeMillis());
        }

        return (check);
    }

}