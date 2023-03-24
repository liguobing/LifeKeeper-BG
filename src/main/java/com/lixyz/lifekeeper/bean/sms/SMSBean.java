package com.lixyz.lifekeeper.bean.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SMSBean {

    @JsonProperty("ObjectId")
    private String objectId;

    @JsonProperty("SMSId")
    private String sMSId;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("SMSCode")
    private String sMSCode;

    @JsonProperty("SMSStatus")
    private int sMSStatus;

    @JsonProperty("SMSType")
    private int sMSType;

    @JsonProperty("CreateTime")
    private long createTime;

    @JsonProperty("VerifyTime")
    private long verifyTime;

    @JsonProperty("PositionNum")
    private long positionNum;

    @JsonProperty("UserId")
    private String userId;

}
