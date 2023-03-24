package com.lixyz.lifekeeper.bean.sms.yotest;

public class Data {

    private int status;
    private double score;
    private String visitorId;
    private String platform;
    private IpInfo ipInfo;
    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setScore(double score) {
        this.score = score;
    }
    public double getScore() {
        return score;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }
    public String getVisitorId() {
        return visitorId;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public String getPlatform() {
        return platform;
    }

    public void setIpInfo(IpInfo ipInfo) {
        this.ipInfo = ipInfo;
    }
    public IpInfo getIpInfo() {
        return ipInfo;
    }

}