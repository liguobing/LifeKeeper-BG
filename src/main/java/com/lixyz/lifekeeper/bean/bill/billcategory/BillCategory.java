package com.lixyz.lifekeeper.bean.bill.billcategory;

public class BillCategory {
    /**
     * ObjectId 唯一标识
     */
    private String objectId;
    /**
     * 分类ID
     */
    private String categoryId;
    /**
     * 用户 ID
     */
    private String categoryUser;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 收入/支出
     */
    private int isIncome;
    /**
     * 分类状态
     * 1：正常分类
     * -1：非正常分类
     */
    private int categoryStatus;
    /**
     * 分类类别
     * 0：正常
     * 1：已删除
     * 2：已修改
     */
    private int categoryType;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryUser() {
        return categoryUser;
    }

    public void setCategoryUser(String categoryUser) {
        this.categoryUser = categoryUser;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getIsIncome() {
        return isIncome;
    }

    public void setIsIncome(int isIncome) {
        this.isIncome = isIncome;
    }

    public int getCategoryStatus() {
        return categoryStatus;
    }

    public void setCategoryStatus(int categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
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
