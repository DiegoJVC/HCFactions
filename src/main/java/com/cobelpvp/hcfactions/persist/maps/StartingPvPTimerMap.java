package com.cobelpvp.hcfactions.persist.maps;

import com.cobelpvp.hcfactions.persist.PersistMap;
import java.util.UUID;

public class StartingPvPTimerMap extends PersistMap<Boolean> {

    public StartingPvPTimerMap() {
        super("StartingTimer", "StartingTimer");
    }

    @Override
    public String getRedisValue(Boolean value) {
        return value.toString();
    }

    @Override
    public Object getMongoValue(Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean getJavaObject(String string) {
        return Boolean.valueOf(string);
    }

    public void set(UUID uuid, boolean value) {
        updateValueAsync(uuid, value);
    }

    public boolean get(UUID uuid) {
        return contains(uuid) && getValue(uuid);
    }

}
