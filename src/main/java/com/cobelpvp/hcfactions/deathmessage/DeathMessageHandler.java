package com.cobelpvp.hcfactions.deathmessage;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.deathmessage.listeners.DamageListener;
import com.cobelpvp.hcfactions.deathmessage.objects.Damage;
import com.cobelpvp.hcfactions.deathmessage.trackers.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeathMessageHandler {

    private static Map<String, List<Damage>> damage = new HashMap<>();

    public static void init() {
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new DamageListener(), HCFactions.getInstance());

        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new GeneralTracker(), HCFactions.getInstance());
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new PVPTracker(), HCFactions.getInstance());
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new EntityTracker(), HCFactions.getInstance());
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new FallTracker(), HCFactions.getInstance());
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new ArrowTracker(), HCFactions.getInstance());
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new VoidTracker(), HCFactions.getInstance());
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new BurnTracker(), HCFactions.getInstance());
    }

    public static List<Damage> getDamage(Player player) {
        return (damage.get(player.getName()));
    }

    public static void addDamage(Player player, Damage addedDamage) {
        if (!damage.containsKey(player.getName())) {
            damage.put(player.getName(), new ArrayList<>());
        }

        List<Damage> damageList = damage.get(player.getName());

        while (damageList.size() > 30) {
            damageList.remove(0);
        }

        damageList.add(addedDamage);
    }

    public static void clearDamage(Player player) {
        damage.remove(player.getName());
    }

}