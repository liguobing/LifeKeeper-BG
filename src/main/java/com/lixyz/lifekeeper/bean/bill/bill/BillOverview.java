package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.ArrayList;

public class BillOverview {
    private int year;
    private int month;
    private double incomeCount;
    private double expendCount;
    private ArrayList<BillDayGroupBean> list;

    public ArrayList<BillDayGroupBean> getList() {
        return list;
    }

    public void setList(ArrayList<BillDayGroupBean> list) {
        this.list = list;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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
}
