package com.cobelpvp.hcfactions.pvpclass.mainclasses.bard;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class BardEffect {

    @Getter private PotionEffect potionEffect;
    @Getter private int energy;

    @Getter private Map<String, Long> lastMessageSent = new HashMap<>();

    public static BardEffect fromPotion(PotionEffect potionEffect) {
        return (new BardEffect(potionEffect, -1));
    }

    public static BardEffect fromPotionAndEnergy(PotionEffect potionEffect, int energy) {
        return (new BardEffect(potionEffect, energy));
    }

    public static BardEffect fromEnergy(int energy) {
        return (new BardEffect(null, energy));
    }

    private BardEffect(PotionEffect potionEffect, int energy) {
        this.potionEffect = potionEffect;
        this.energy = energy;
    }

}