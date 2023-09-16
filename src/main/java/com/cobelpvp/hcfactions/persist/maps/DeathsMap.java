package com.cobelpvp.hcfactions.persist.maps;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.persist.PersistMap;
import java.util.UUID;

public class DeathsMap extends PersistMap<Integer> {

    public DeathsMap() {
        super("Deaths", "Deaths");
    }

    @Override
    public String getRedisValue(Integer deaths) {
        return (String.valueOf(deaths));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer deaths) {
        return (deaths);
    }

    public int getDeaths(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setDeaths(UUID update, int kills) {
        updateValueAsync(update, kills);
        HCFactions.getInstance().getKdrMap().updateKDR(update);
    }

}
