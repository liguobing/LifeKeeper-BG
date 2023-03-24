package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.ArrayList;

public class BillWebDataBean {
    private int pageCount;
    private ArrayList<Bill> bills;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public ArrayList<Bill> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        this.bills = bills;
    }
}
