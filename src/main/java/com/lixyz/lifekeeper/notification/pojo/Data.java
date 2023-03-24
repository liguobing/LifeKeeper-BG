package com.lixyz.lifekeeper.notification.pojo;

public class Data {

    private com.lixyz.lifekeeper.notification.pojo.PlanName PlanName;
    private com.lixyz.lifekeeper.notification.pojo.PlanTime PlanTime;
    private com.lixyz.lifekeeper.notification.pojo.PlanLocation PlanLocation;
    private com.lixyz.lifekeeper.notification.pojo.PlanDescription PlanDescription;

    public PlanName getPlanName() {
        return PlanName;
    }

    public void setPlanName(PlanName planName) {
        PlanName = planName;
    }

    public PlanTime getPlanTime() {
        return PlanTime;
    }

    public void setPlanTime(PlanTime planTime) {
        PlanTime = planTime;
    }

    public PlanLocation getPlanLocation() {
        return PlanLocation;
    }

    public void setPlanLocation(PlanLocation planLocation) {
        PlanLocation = planLocation;
    }

    public PlanDescription getPlanDescription() {
        return PlanDescription;
    }

    public void setPlanDescription(PlanDescription planDescription) {
        PlanDescription = planDescription;
    }
}
