package com.lixyz.lifekeeper.bean.bill.bill;

import java.util.ArrayList;

public class Bill {
    /**
     * ObjectId
     */
    private String objectId;
    /**
     * BillId
     */
    private String billId;
    /**
     * 账单日期
     */
    private long billDate;
    /**
     * 账单金额
     */
    private Double billMoney;
    /**
     * 账单属性
     * -1：支出
     * 1：收入
     */
    private int billProperty;
    /**
     * 账单分类
     */
    private String billCategory;
    /**
     * 账单账户
     */
    private String billAccount;
    /**
     * 账单备注
     */
    private String billRemark;
    /**
     * 账单用户
     */
    private String billUser;
    /**
     * 账单商家
     */
    private String billShop;
    /**
     * 账单状态
     * -1：非正常
     * 1：正常
     */
    private int billStatus;
    /**
     * 账单类型
     * 0：正常
     * 1：已删除
     * 2：已修改
     */
    private int billType;
    /**
     * 账单图片
     */
    private ArrayList<String> billImage;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public long getBillDate() {
        return billDate;
    }

    public void setBillDate(long billDate) {
        this.billDate = billDate;
    }

    public Double getBillMoney() {
        return billMoney;
    }

    public void setBillMoney(Double billMoney) {
        this.billMoney = billMoney;
    }

    public int getBillProperty() {
        return billProperty;
    }

    public void setBillProperty(int billProperty) {
        this.billProperty = billProperty;
    }

    public String getBillCategory() {
        return billCategory;
    }

    public void setBillCategory(String billCategory) {
        this.billCategory = billCategory;
    }

    public String getBillAccount() {
        return billAccount;
    }

    public void setBillAccount(String billAccount) {
        this.billAccount = billAccount;
    }

    public String getBillRemark() {
        return billRemark;
    }

    public void setBillRemark(String billRemark) {
        this.billRemark = billRemark;
    }

    public String getBillUser() {
        return billUser;
    }

    public void setBillUser(String billUser) {
        this.billUser = billUser;
    }

    public String getBillShop() {
        return billShop;
    }

    public void setBillShop(String billShop) {
        this.billShop = billShop;
    }

    public int getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(int billStatus) {
        this.billStatus = billStatus;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public ArrayList<String> getBillImage() {
        return billImage;
    }

    public void setBillImage(ArrayList<String> billImage) {
        this.billImage = billImage;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
