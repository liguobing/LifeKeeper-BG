package com.lixyz.lifekeeper.bean.getui;

public class AuthDataResponseBean {
    private String expireTime;
    private String token;

    public AuthDataResponseBean() {
    }

    public AuthDataResponseBean(String expireTime, String token) {
        this.expireTime = expireTime;
        this.token = token;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
