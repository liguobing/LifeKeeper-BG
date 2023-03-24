package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.ArrayList;

public class BillDayGroupBean {
    private ArrayList<Bill> bills;
    private double incomeCountForDay;
    private double expendCountForDay;
    private String date;

    public ArrayList<Bill> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        this.bills = bills;
    }

    public double getIncomeCountForDay() {
        return incomeCountForDay;
    }

    public void setIncomeCountForDay(double incomeCountForDay) {
        this.incomeCountForDay = incomeCountForDay;
    }

    public double getExpendCountForDay() {
        return expendCountForDay;
    }

    public void setExpendCountForDay(double expendCountForDay) {
        this.expendCountForDay = expendCountForDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
