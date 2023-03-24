package com.lixyz.lifekeeper.bean.getui;

public class Settings {
    private long ttl;

    public Settings(long ttl) {
        this.ttl = ttl;
    }

    public Settings() {
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
