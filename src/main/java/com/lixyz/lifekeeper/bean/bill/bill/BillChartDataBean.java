package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.ArrayList;

public class BillChartDataBean {
    private long start;
    private long end;
    /**
     * 收入 - 用于计算百分比
     */
    private double income;
    /**
     * 支出 - 用于计算百分比
     */
    private double expend;

    private ArrayList<BillMoneyCountForCategoryGroup> incomeCategoryData;

    private ArrayList<BillMoneyCountForCategoryGroup> expendCategoryData;

    private ArrayList<BillMoneyCountForDay> billMoneyCountForDays;

    public ArrayList<BillMoneyCountForDay> getBillMoneyCountForDays() {
        return billMoneyCountForDays;
    }

    public void setBillMoneyCountForDays(ArrayList<BillMoneyCountForDay> billMoneyCountForDays) {
        this.billMoneyCountForDays = billMoneyCountForDays;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpend() {
        return expend;
    }

    public void setExpend(double expend) {
        this.expend = expend;
    }

    public ArrayList<BillMoneyCountForCategoryGroup> getIncomeCategoryData() {
        return incomeCategoryData;
    }

    public void setIncomeCategoryData(ArrayList<BillMoneyCountForCategoryGroup> incomeCategoryData) {
        this.incomeCategoryData = incomeCategoryData;
    }

    public ArrayList<BillMoneyCountForCategoryGroup> getExpendCategoryData() {
        return expendCategoryData;
    }

    public void setExpendCategoryData(ArrayList<BillMoneyCountForCategoryGroup> expendCategoryData) {
        this.expendCategoryData = expendCategoryData;
    }
}
