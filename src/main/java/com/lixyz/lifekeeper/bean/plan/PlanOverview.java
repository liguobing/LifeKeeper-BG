package com.lixyz.lifekeeper.bean.plan;

public class PlanOverview {
    private int planCountOfDay;
    private int planCountOfMonth;

    public PlanOverview() {
    }

    public PlanOverview(int planCountOfDay, int planCountOfMonth) {
        this.planCountOfDay = planCountOfDay;
        this.planCountOfMonth = planCountOfMonth;
    }

    public int getPlanCountOfDay() {
        return planCountOfDay;
    }

    public void setPlanCountOfDay(int planCountOfDay) {
        this.planCountOfDay = planCountOfDay;
    }

    public int getPlanCountOfMonth() {
        return planCountOfMonth;
    }

    public void setPlanCountOfMonth(int planCountOfMonth) {
        this.planCountOfMonth = planCountOfMonth;
    }
}
