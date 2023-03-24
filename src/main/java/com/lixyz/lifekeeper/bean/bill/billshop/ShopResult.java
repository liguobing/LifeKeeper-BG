package com.lixyz.lifekeeper.bean.bill.billshop;

import java.util.ArrayList;

public class ShopResult {
    private int shopCount;
    private int offset;
    private ArrayList<String> shopNames;

    public int getShopCount() {
        return shopCount;
    }

    public void setShopCount(int shopCount) {
        this.shopCount = shopCount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public ArrayList<String> getShopNames() {
        return shopNames;
    }

    public void setShopNames(ArrayList<String> shopNames) {
        this.shopNames = shopNames;
    }
}