package com.lixyz.lifekeeper.bean.bill.billaccount;

public class BillAccountOverview {
    private Double incomeCount;
    private Double expendCount;

    public BillAccountOverview() {
    }

    public BillAccountOverview(Double incomeCount, Double expendCount) {
        this.incomeCount = incomeCount;
        this.expendCount = expendCount;
    }

    public Double getIncomeCount() {
        return incomeCount;
    }

    public void setIncomeCount(Double incomeCount) {
        this.incomeCount = incomeCount;
    }

    public Double getExpendCount() {
        return expendCount;
    }

    public void setExpendCount(Double expendCount) {
        this.expendCount = expendCount;
    }
}
