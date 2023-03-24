package com.lixyz.lifekeeper.bean.bill.billshop;

public class BillShop {
    /**
     * ObjectId
     */
    private String objectId;
    /**
     * 商家 ID
     */
    private String shopId;
    /**
     * 商家名称
     */
    private String shopName;
    /**
     * 商家图标
     */
    private String shopIcon;
    /**
     * 商家用户
     */
    private String shopUser;
    /**
     * 商家状态
     * 1：正常商家
     * -1：非正常商家
     */
    private int shopStatus;
    /**
     * 商家类别
     * 0：正常
     * 1：已删除
     * 2：已修改
     */
    private int shopType;
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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopIcon() {
        return shopIcon;
    }

    public void setShopIcon(String shopIcon) {
        this.shopIcon = shopIcon;
    }

    public String getShopUser() {
        return shopUser;
    }

    public void setShopUser(String shopUser) {
        this.shopUser = shopUser;
    }

    public int getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(int shopStatus) {
        this.shopStatus = shopStatus;
    }

    public int getShopType() {
        return shopType;
    }

    public void setShopType(int shopType) {
        this.shopType = shopType;
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
