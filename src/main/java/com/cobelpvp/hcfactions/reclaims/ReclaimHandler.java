package com.cobelpvp.hcfactions.reclaims;

import com.cobelpvp.atheneum.Atheneum;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class ReclaimHandler {

    private final File dataFile = new File(Atheneum.getInstance().getDataFolder(), "reclaims.json");
    private final Type dataType = new TypeToken<Set<UUID>>() {}.getType();

    private Set<UUID> hasReclaimed = new HashSet<>();

    public ReclaimHandler() {
        loadData();
    }

    public void loadData() {
        if (dataFile.exists()) {
            try (Reader reader = Files.newReader(dataFile, Charsets.UTF_8)) {
                hasReclaimed = Atheneum.PLAIN_GSON.fromJson(reader, dataType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Atheneum.getInstance(), this::saveData, 20L * 60L, 20L * 60L);
    }

    private void saveData() {
        try {
            Files.write(Atheneum.PLAIN_GSON.toJson(hasReclaimed), dataFile, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
