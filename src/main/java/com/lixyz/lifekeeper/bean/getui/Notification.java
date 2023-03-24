package com.lixyz.lifekeeper.bean.getui;

public class Notification {
    private String title;
    private String body;
    private String click_type;
    private String intent;

    public Notification() {
    }

    public Notification(String title, String body, String click_type, String intent) {
        this.title = title;
        this.body = body;
        this.click_type = click_type;
        this.intent = intent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getClick_type() {
        return click_type;
    }

    public void setClick_type(String click_type) {
        this.click_type = click_type;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
