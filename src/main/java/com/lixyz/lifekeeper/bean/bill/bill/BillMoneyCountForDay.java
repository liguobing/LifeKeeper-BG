package com.lixyz.lifekeeper.bean.bill.bill;

public class BillMoneyCountForDay {
    private String date;
    private double income;
    private double expend;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
