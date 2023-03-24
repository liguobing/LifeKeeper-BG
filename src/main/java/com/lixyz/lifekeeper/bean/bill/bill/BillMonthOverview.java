package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.List;

public class BillMonthOverview {
    private int year;
    private int month;
    private double incomeCount;
    private double expendCount;
    private List<Bill> bills;

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

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
