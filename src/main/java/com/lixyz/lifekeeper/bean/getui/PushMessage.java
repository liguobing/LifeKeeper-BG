package com.lixyz.lifekeeper.bean.getui;

public class PushMessage {
    private Notification notification;

    public PushMessage(Notification notification) {
        this.notification = notification;
    }

    public PushMessage() {
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
