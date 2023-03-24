package com.lixyz.lifekeeper.bean.getui;

public class AuthResponseBean {
    private String msg;
    private int code;
    private AuthDataResponseBean data;

    public AuthResponseBean() {
    }

    public AuthResponseBean(String msg, int code, AuthDataResponseBean data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AuthDataResponseBean getData() {
        return data;
    }

    public void setData(AuthDataResponseBean data) {
        this.data = data;
    }
}
