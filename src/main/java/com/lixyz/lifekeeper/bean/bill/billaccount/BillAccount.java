package com.lixyz.lifekeeper.bean.bill.billaccount;

public class BillAccount {
    /**
     * ObjectId
     */
    private String objectId;
    /**
     * 账户 ID
     */
    private String accountId;
    /**
     * 账户用户
     */
    private String accountUser;
    /**
     * 账户名
     */
    private String accountName;
    /**
     * 账户状态
     * 1：正常账户
     * -1：非正常账户
     */
    private int accountStatus;
    /**
     * 账户类型
     * 0：正常账户
     * 1：已删除账户
     * 2：已修改账户
     */
    private int accountType;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 排序下标
     */
    private int orderIndex;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
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

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
