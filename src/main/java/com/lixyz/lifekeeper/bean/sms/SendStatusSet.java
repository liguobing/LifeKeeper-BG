package com.lixyz.lifekeeper.bean.sms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendStatusSet {
    private String SerialNo;
    private String PhoneNumber;
    private int Fee;
    private String SessionContext;
    private String Code;
    private String Message;
    private String IsoCode;

    public SendStatusSet() {
    }

    public SendStatusSet(String serialNo, String phoneNumber, int fee, String sessionContext, String code, String message, String isoCode) {
        SerialNo = serialNo;
        PhoneNumber = phoneNumber;
        Fee = fee;
        SessionContext = sessionContext;
        Code = code;
        Message = message;
        IsoCode = isoCode;
    }

    public String getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(String serialNo) {
        SerialNo = serialNo;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getFee() {
        return Fee;
    }

    public void setFee(int fee) {
        Fee = fee;
    }

    public String getSessionContext() {
        return SessionContext;
    }

    public void setSessionContext(String sessionContext) {
        SessionContext = sessionContext;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getIsoCode() {
        return IsoCode;
    }

    public void setIsoCode(String isoCode) {
        IsoCode = isoCode;
    }
}
