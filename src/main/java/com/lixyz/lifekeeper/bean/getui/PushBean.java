package com.lixyz.lifekeeper.bean.getui;

public class PushBean {
    private String request_id;
    private Settings settings;
    private Audience audience;
    private PushMessage push_message;

    public PushBean() {
    }

    public PushBean(String request_id, Settings settings, Audience audience, PushMessage push_message) {
        this.request_id = request_id;
        this.settings = settings;
        this.audience = audience;
        this.push_message = push_message;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }

    public PushMessage getPush_message() {
        return push_message;
    }

    public void setPush_message(PushMessage push_message) {
        this.push_message = push_message;
    }
}
