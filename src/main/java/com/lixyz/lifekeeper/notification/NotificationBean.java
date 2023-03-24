package com.lixyz.lifekeeper.notification;

public class NotificationBean {
    private String objectId;
    private String groupId;
    private String planName;
    private long time;
    private String formatTime;

    public NotificationBean() {
    }

    public NotificationBean(String objectId, String groupId, String planName, long time, String formatTime) {
        this.objectId = objectId;
        this.groupId = groupId;
        this.planName = planName;
        this.time = time;
        this.formatTime = formatTime;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }
}
