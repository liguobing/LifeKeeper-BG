package com.lixyz.lifekeeper.bean.getui;

public class AuthBean {
    private String sign;
    private String timestamp;
    private String appkey;

    public AuthBean(String sign, String timestamp, String appkey) {
        this.sign = sign;
        this.timestamp = timestamp;
        this.appkey = appkey;
    }

    public AuthBean() {
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
