package com.lixyz.lifekeeper.bean.getui;

public class Audience {
    private String[] cid;

    public Audience(String[] cid) {
        this.cid = cid;
    }

    public Audience() {
    }

    public String[] getCid() {
        return cid;
    }

    public void setCid(String[] cid) {
        this.cid = cid;
    }
}
