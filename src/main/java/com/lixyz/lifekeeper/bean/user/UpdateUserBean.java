package com.lixyz.lifekeeper.bean.user;

public class UpdateUserBean {
    /**
     * 需要更新的用户的 ObjectId
     */
    private String oldUserObjectId;
    /**
     * 更新时间
     */
    private long oldUserUpdateTime;
    /**
     * 更新后的用户对象
     */
    private UserBean newUser;

    public String getOldUserObjectId() {
        return oldUserObjectId;
    }

    public void setOldUserObjectId(String oldUserObjectId) {
        this.oldUserObjectId = oldUserObjectId;
    }


    public long getOldUserUpdateTime() {
        return oldUserUpdateTime;
    }

    public void setOldUserUpdateTime(long oldUserUpdateTime) {
        this.oldUserUpdateTime = oldUserUpdateTime;
    }

    public UserBean getNewUser() {
        return newUser;
    }

    public void setNewUser(UserBean newUser) {
        this.newUser = newUser;
    }
}
