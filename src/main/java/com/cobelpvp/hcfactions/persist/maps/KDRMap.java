package com.cobelpvp.hcfactions.persist.maps;

import java.util.UUID;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.persist.PersistMap;

public class KDRMap extends PersistMap<Double> {

    public KDRMap() {
        super("KDR", "KDR");
    }

    @Override
    public String getRedisValue(Double kdr) {
        return (String.valueOf(kdr));
    }

    @Override
    public Double getJavaObject(String str) {
        return (Double.parseDouble(str));
    }

    @Override
    public Object getMongoValue(Double kdr) {
        return (kdr);
    }

    public void setKDR(UUID update, double kdr) {
        updateValueAsync(update, kdr);
    }

    public void updateKDR(UUID update) {
        setKDR(update, Math.max(((double) HCFactions.getInstance().getKillsMap().getKills(update)) / Math.max(HCFactions.getInstance().getDeathsMap().getDeaths(update), 1), 0));
    }
}
