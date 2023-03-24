package com.lixyz.lifekeeper.bean.sms;

import java.util.List;

public class SMSResponseBean {

    private SendStatusSet[] SendStatusSet;
    private String RequestId;

    public SMSResponseBean() {
    }

    public SMSResponseBean(SendStatusSet[] sendStatusSet, String requestId) {
        this.SendStatusSet = sendStatusSet;
        this.RequestId = requestId;
    }

    public SendStatusSet[] getSendStatusSet() {
        return SendStatusSet;
    }

    public void setSendStatusSet(SendStatusSet[] sendStatusSet) {
        this.SendStatusSet = sendStatusSet;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        this.RequestId = requestId;
    }
}
