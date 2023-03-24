package com.lixyz.lifekeeper.bean.bill.billshop;

public class UpdateBillShopBean {
    private BillShop billShop;
    private String NewObjectId;

    public BillShop getBillShop() {
        return billShop;
    }

    public void setBillShop(BillShop billShop) {
        this.billShop = billShop;
    }

    public String getNewObjectId() {
        return NewObjectId;
    }

    public void setNewObjectId(String newObjectId) {
        NewObjectId = newObjectId;
    }
}
