package com.cobelpvp.hcfactions.persist.maps;

import java.util.UUID;
import com.cobelpvp.hcfactions.persist.PersistMap;

public class LivesMap extends PersistMap<Integer> {

    public LivesMap() {
        super("Lives", "Lives");
    }

    @Override
    public String getRedisValue(Integer lives) {
        return (String.valueOf(lives));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer lives) {
        return (lives);
    }

    public int getLives(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setLives(UUID update, int lives) {
        updateValueSync(update, lives);
    }

}