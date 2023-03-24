package com.lixyz.lifekeeper.bean.bill.bill;

import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;

import java.util.ArrayList;

public class BillCategoryAndBillAccount {
    private ArrayList<BillAccount> billAccounts;
    private ArrayList<BillCategory> allBillCategories;
    private ArrayList<BillCategory> incomeBillCategories;
    private ArrayList<BillCategory> expendBillCategories;

    public ArrayList<BillAccount> getBillAccounts() {
        return billAccounts;
    }

    public void setBillAccounts(ArrayList<BillAccount> billAccounts) {
        this.billAccounts = billAccounts;
    }

    public ArrayList<BillCategory> getAllBillCategories() {
        return allBillCategories;
    }

    public void setAllBillCategories(ArrayList<BillCategory> allBillCategories) {
        this.allBillCategories = allBillCategories;
    }

    public ArrayList<BillCategory> getIncomeBillCategories() {
        return incomeBillCategories;
    }

    public void setIncomeBillCategories(ArrayList<BillCategory> incomeBillCategories) {
        this.incomeBillCategories = incomeBillCategories;
    }

    public ArrayList<BillCategory> getExpendBillCategories() {
        return expendBillCategories;
    }

    public void setExpendBillCategories(ArrayList<BillCategory> expendBillCategories) {
        this.expendBillCategories = expendBillCategories;
    }
}
