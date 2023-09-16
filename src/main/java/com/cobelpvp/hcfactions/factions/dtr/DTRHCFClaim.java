package com.cobelpvp.hcfactions.factions.dtr;

import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import lombok.Getter;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.Location;

public enum DTRHCFClaim {

    // Used in spawns
    SAFE_ZONE(1, "Safe-Zone", "Determines if a region is considered completely safe"),

    // Used in the end spawn
    DENY_REENTRY(2, "Deny-Reentry", "Determines if a region can be reentered"),

    // Used in Citadel
    FIFTEEN_MINUTE_DEATHBAN(4, "15m-Deathban", "Determines if a region has a 15m deathban"),

    // Used in KOTHs
    FIVE_MINUTE_DEATHBAN(8, "5m-Deathban", "Determines if a region has a 5m deathban"),

    // Used in Citadel
    THIRTY_SECOND_ENDERPEARL_COOLDOWN(16, "30s-Enderpearl-Cooldown", "Determines if a region has a 30s enderpearl cooldown"),

    // Used in Citadel
    CITADEL(32, "Citadel", "Determines if a region is part of Citadel"),

    // Used in KOTHs
    KOTH(64, "KOTH", "Determines if a region is a KOTH"),

    // Used in KOTHs & Citadel.
    REDUCED_DTR_LOSS(128, "Reduced-DTR-Loss", "Determines if a region takes away reduced DTR upon death"),

    // Used in various regions.
    NO_ENDERPEARL(256, "No-Enderpearl", "Determines if a region cannot be pearled into"),

    // Used in various regions.
    QUARTER_DTR_LOSS(512, "1/4-DTR-Loss", "Determines if a region takes away 1/4th DTR loss."),

    // Used on the road.
    ROAD(1024, "Road", "Determines if a region is a road."),

    DUEL(4096, "Duel", "Determines if a region is part of a duel"),

    DTC(8192, "DTC", "Determines if a region is part of DTC");

    @Getter
    private int hcfclaim;
    @Getter
    private String name;
    @Getter
    private String description;

    DTRHCFClaim(int hcfclaim, String name, String description) {
        this.hcfclaim = hcfclaim;
        this.name = name;
        this.description = description;
    }

    public boolean appliesAt(Location location) {
        Faction ownerTo = LandBoard.getInstance().getTeam(location);
        return (ownerTo != null && ownerTo.getOwner() == null && ownerTo.hasDTRHCFClaim(this));
    }

}