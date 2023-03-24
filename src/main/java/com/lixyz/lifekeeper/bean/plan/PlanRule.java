package com.lixyz.lifekeeper.bean.plan;

import java.io.Serializable;

public class PlanRule implements Serializable {
    private String objectId;
    private String ruleId;
    /**
     * 1：全天
     * -1：非全天
     */
    private int isAllDay;
    private String planName;
    private String planDescription;
    private String planLocation;
    private String planRemark;
    private String planUser;
    private long startTime;
    private int alarmTime;
    /**
     * 1：一次性活动
     * 2：每天
     * 3：每周
     * 4：每月
     * 5：每年
     */
    private int repeatType;
    /**
     * 1:时间
     * 2:次数
     */
    private int endRepeatType;
    /**
     * 根据 endRepeatType 值确定
     * endRepeatType = 1 时，该字段表示结束时间
     * endRepeatType = 2 时，该字段表示重复次数
     */
    private long endRepeatValue;
    private long createTime;
    private long updateTime;

    public PlanRule() {
    }

    public PlanRule(String objectId, String ruleId, int isAllDay, String planName, String planDescription, String planLocation, String planRemark, String planUser, long startTime, int alarmTime, int repeatType, int endRepeatType, long endRepeatValue, long createTime, long updateTime) {
        this.objectId = objectId;
        this.ruleId = ruleId;
        this.isAllDay = isAllDay;
        this.planName = planName;
        this.planDescription = planDescription;
        this.planLocation = planLocation;
        this.planRemark = planRemark;
        this.planUser = planUser;
        this.startTime = startTime;
        this.alarmTime = alarmTime;
        this.repeatType = repeatType;
        this.endRepeatType = endRepeatType;
        this.endRepeatValue = endRepeatValue;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public int getIsAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(int isAllDay) {
        this.isAllDay = isAllDay;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public String getPlanLocation() {
        return planLocation;
    }

    public void setPlanLocation(String planLocation) {
        this.planLocation = planLocation;
    }

    public String getPlanRemark() {
        return planRemark;
    }

    public void setPlanRemark(String planRemark) {
        this.planRemark = planRemark;
    }

    public String getPlanUser() {
        return planUser;
    }

    public void setPlanUser(String planUser) {
        this.planUser = planUser;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(int alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public int getEndRepeatType() {
        return endRepeatType;
    }

    public void setEndRepeatType(int endRepeatType) {
        this.endRepeatType = endRepeatType;
    }

    public long getEndRepeatValue() {
        return endRepeatValue;
    }

    public void setEndRepeatValue(long endRepeatValue) {
        this.endRepeatValue = endRepeatValue;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
