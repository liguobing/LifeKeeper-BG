package com.lixyz.lifekeeper.bean.bill.bill;

public class BillMoneyCountForCategoryGroup {
    private String billCategory;
    private double moneyCount;
    private int isIncome;

    public int getIsIncome() {
        return isIncome;
    }

    public void setIsIncome(int isIncome) {
        this.isIncome = isIncome;
    }

    public String getBillCategory() {
        return billCategory;
    }

    public void setBillCategory(String billCategory) {
        this.billCategory = billCategory;
    }

    public double getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(double moneyCount) {
        this.moneyCount = moneyCount;
    }
}

