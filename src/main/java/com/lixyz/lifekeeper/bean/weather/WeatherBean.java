package com.lixyz.lifekeeper.bean.weather;

import java.util.Date;

public class WeatherBean {

    private String code;
    private Date updateTime;
    private String fxLink;
    private Now now;
    private Refer refer;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setFxLink(String fxLink) {
        this.fxLink = fxLink;
    }

    public String getFxLink() {
        return fxLink;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public Now getNow() {
        return now;
    }

    public void setRefer(Refer refer) {
        this.refer = refer;
    }

    public Refer getRefer() {
        return refer;
    }

}
