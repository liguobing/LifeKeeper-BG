package com.lixyz.lifekeeper.bean;

import com.lixyz.lifekeeper.bean.user.UserBean;

public class Overview {
    private UserBean userBean;
    private double incomeCount;
    private double expendCount;
    private int planCountOfDay;
    private int planCountOfMonth;
    private int fileCount;

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public double getIncomeCount() {
        return incomeCount;
    }

    public void setIncomeCount(double incomeCount) {
        this.incomeCount = incomeCount;
    }

    public double getExpendCount() {
        return expendCount;
    }

    public void setExpendCount(double expendCount) {
        this.expendCount = expendCount;
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

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
}
