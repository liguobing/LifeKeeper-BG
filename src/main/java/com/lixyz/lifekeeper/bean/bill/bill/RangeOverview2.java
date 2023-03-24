package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.ArrayList;

public class RangeOverview2 {
    private int type;
    private String start;
    private String end;
    private double income;
    private double expend;
    private ArrayList<Bill> incomeTop10;
    private ArrayList<Bill> expendTop10;
    private ArrayList<BillMoneyCountForCategoryGroup> allCategoryTop10;
    private ArrayList<BillMoneyCountForCategoryGroup> incomeCategoryTop10;
    private ArrayList<BillMoneyCountForCategoryGroup> expendCategoryTop10;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
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


    public ArrayList<Bill> getIncomeTop10() {
        return incomeTop10;
    }

    public void setIncomeTop10(ArrayList<Bill> incomeTop10) {
        this.incomeTop10 = incomeTop10;
    }

    public ArrayList<Bill> getExpendTop10() {
        return expendTop10;
    }

    public void setExpendTop10(ArrayList<Bill> expendTop10) {
        this.expendTop10 = expendTop10;
    }

    public ArrayList<BillMoneyCountForCategoryGroup> getAllCategoryTop10() {
        return allCategoryTop10;
    }

    public void setAllCategoryTop10(ArrayList<BillMoneyCountForCategoryGroup> allCategoryTop10) {
        this.allCategoryTop10 = allCategoryTop10;
    }

    public ArrayList<BillMoneyCountForCategoryGroup> getIncomeCategoryTop10() {
        return incomeCategoryTop10;
    }

    public void setIncomeCategoryTop10(ArrayList<BillMoneyCountForCategoryGroup> incomeCategoryTop10) {
        this.incomeCategoryTop10 = incomeCategoryTop10;
    }

    public ArrayList<BillMoneyCountForCategoryGroup> getExpendCategoryTop10() {
        return expendCategoryTop10;
    }

    public void setExpendCategoryTop10(ArrayList<BillMoneyCountForCategoryGroup> expendCategoryTop10) {
        this.expendCategoryTop10 = expendCategoryTop10;
    }
}
