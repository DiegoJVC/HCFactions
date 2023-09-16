package com.cobelpvp.hcfactions.deathmessage.util;

import com.cobelpvp.hcfactions.deathmessage.objects.Damage;

public class UnknownDamage extends Damage {

    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    public String getDeathMessage() {
        return (wrapName(getDamaged()) + " died.");
    }

}