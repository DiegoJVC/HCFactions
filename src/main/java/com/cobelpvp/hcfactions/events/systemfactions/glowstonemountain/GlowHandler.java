package com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain;

import java.io.File;
import java.io.IOException;

import com.cobelpvp.hcfactions.events.systemfactions.glowstonemountain.listeners.GlowListener;
import com.cobelpvp.hcfactions.factions.claims.Claim;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.Atheneum;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class GlowHandler {

    @Getter
    private final static String glowTeamName = "Glowstone";
    private static File file;
    @Getter
    @Setter
    private GlowMountain glowMountain;

    public GlowHandler() {
        try {
            file = new File(HCFactions.getInstance().getDataFolder(), "glowmtn.json");

            if (!file.exists()) {
                glowMountain = null;

                if (file.createNewFile()) {
                    HCFactions.getInstance().getLogger().warning("Created a new glow mountain json file.");
                }
            } else {
                glowMountain = Atheneum.GSON.fromJson(FileUtils.readFileToString(file), GlowMountain.class);
                HCFactions.getInstance().getLogger().info("Successfully loaded the glow mountain from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), HCFactions.getInstance());
    }

    public static Claim getClaim() {
        return HCFactions.getInstance().getFactionHandler().getTeam(glowTeamName).getClaims().get(0); // null if no glowmtn is set!
    }

    public void save() {
        try {
            FileUtils.write(file, Atheneum.GSON.toJson(glowMountain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasGlowMountain() {
        return glowMountain != null;
    }
}