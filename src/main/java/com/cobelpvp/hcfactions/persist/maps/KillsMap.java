package com.cobelpvp.hcfactions.persist.maps;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.persist.PersistMap;
import java.util.UUID;

public class KillsMap extends PersistMap<Integer> {

    public KillsMap() {
        super("Kills", "Kills");
    }

    @Override
    public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer kills) {
        return (kills);
    }

    public int getKills(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setKills(UUID update, int kills) {
        updateValueAsync(update, kills);
        HCFactions.getInstance().getKdrMap().updateKDR(update);
    }

}