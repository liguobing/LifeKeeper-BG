package com.lixyz.lifekeeper.bean.plan;

public class PlanBean {
    /**
     * ObjectId
     */
    private String objectId;
    /**
     * PlanId
     */
    private String planId;
    /**
     * GroupId。如果为-1，则是一次性计划
     */
    private String groupId;
    /**
     * 是否是全天计划
     */
    private int isAllDay;
    /**
     * 计划名称
     */
    private String planName;
    /**
     * 计划描述
     */
    private String planDescription;
    /**
     * 计划执行地点
     */
    private String planLocation;
    /**
     * 用户Id
     */
    private String planUser;
    /**
     * 计划开始时间
     */
    private long startTime;
    /**
     * 提醒时间
     */
    private int alarmTime;
    /**
     * 重复模式
     */
    private int repeatMode;

    /**
     * 第几次执行
     */
    private int repeatIndex;
    /**
     * 重复次数
     */
    private int repeatCount;
    /**
     * 结束重复时间
     */
    private long endRepeatTime;
    /**
     * 是否已经完成
     */
    private int isFinished;
    /**
     * 状态
     */
    private int planStatus;
    /**
     * 类型
     */
    private int planType;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 完成时间
     */
    private long finishTime;

    public int getRepeatIndex() {
        return repeatIndex;
    }

    public void setRepeatIndex(int repeatIndex) {
        this.repeatIndex = repeatIndex;
    }

    public int getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(int alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public long getEndRepeatTime() {
        return endRepeatTime;
    }

    public void setEndRepeatTime(long endRepeatTime) {
        this.endRepeatTime = endRepeatTime;
    }

    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
    }

    public int getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(int planStatus) {
        this.planStatus = planStatus;
    }

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
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

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
}
