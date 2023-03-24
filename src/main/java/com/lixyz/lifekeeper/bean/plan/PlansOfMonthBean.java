package com.lixyz.lifekeeper.bean.plan;

import java.util.ArrayList;

public class PlansOfMonthBean {
    private String date;
    private ArrayList<PlanBean> plans;

    public PlansOfMonthBean() {

    }

    public PlansOfMonthBean(String date, ArrayList<PlanBean> plans) {
        this.date = date;
        this.plans = plans;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<PlanBean> getPlans() {
        return plans;
    }

    public void setPlans(ArrayList<PlanBean> plans) {
        this.plans = plans;
    }
}
