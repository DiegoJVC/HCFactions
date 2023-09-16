package com.cobelpvp.hcfactions.mobstack.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.function.Predicate;

public class EntityWithinDistance implements Predicate<Entity> {

    private final Location here;
    private final double distanceSquared;
    private final Location loc;

    public EntityWithinDistance(Location here, double distance) {
        this.here = here;
        this.distanceSquared = distance * distance;
        this.loc = here.clone();
    }

    @Override
    public boolean test(Entity entity) {
        entity.getLocation(loc);
        return loc.getWorld() == here.getWorld() && here.distanceSquared(loc) < distanceSquared;
    }
}
