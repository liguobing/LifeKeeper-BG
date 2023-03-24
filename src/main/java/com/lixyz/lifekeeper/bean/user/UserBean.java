package com.lixyz.lifekeeper.bean.user;

public class UserBean {
    /**
     * 唯一标识
     */
    private String objectId;
    /**
     * 用户 ID
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 用户头像链接
     */
    private String userIconUrl;
    /**
     * 绑定微博名
     */
    private String userBindWeibo;
    /**
     * 绑定微博 token
     */
    private String userBindWeiboAccessToken;
    /**
     * 绑定微博头像
     */
    private String userBindWeiboIcon;
    /**
     * 绑定微博 token 过期时间
     */
    private String userBindWeiboExpiresTime;
    /**
     * 绑定微博 ID
     */
    private String userBindWeiboId;
    /**
     * 绑定 QQ 名
     */
    private String userBindQQ;
    /**
     * 绑定 QQ Id
     */
    private String userBindQQOpenId;
    /**
     * 绑定 QQ token 过期时间
     */
    private String userBindQQExpiresTime;
    /**
     * 绑定 QQ token
     */
    private String userBindQQAccessToken;
    /**
     * 绑定 QQ 头像链接
     */
    private String userBindQQIcon;
    /**
     * 用户状态
     * 1：正常；
     * -1：非正常；
     */
    private int userStatus;
    /**
     * 用户类型
     * 0：正常
     * 1：已删除
     * 2：已修改
     */
    private int userType;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public String getUserBindWeibo() {
        return userBindWeibo;
    }

    public void setUserBindWeibo(String userBindWeibo) {
        this.userBindWeibo = userBindWeibo;
    }

    public String getUserBindWeiboAccessToken() {
        return userBindWeiboAccessToken;
    }

    public void setUserBindWeiboAccessToken(String userBindWeiboAccessToken) {
        this.userBindWeiboAccessToken = userBindWeiboAccessToken;
    }

    public String getUserBindWeiboIcon() {
        return userBindWeiboIcon;
    }

    public void setUserBindWeiboIcon(String userBindWeiboIcon) {
        this.userBindWeiboIcon = userBindWeiboIcon;
    }

    public String getUserBindWeiboExpiresTime() {
        return userBindWeiboExpiresTime;
    }

    public void setUserBindWeiboExpiresTime(String userBindWeiboExpiresTime) {
        this.userBindWeiboExpiresTime = userBindWeiboExpiresTime;
    }

    public String getUserBindWeiboId() {
        return userBindWeiboId;
    }

    public void setUserBindWeiboId(String userBindWeiboId) {
        this.userBindWeiboId = userBindWeiboId;
    }

    public String getUserBindQQ() {
        return userBindQQ;
    }

    public void setUserBindQQ(String userBindQQ) {
        this.userBindQQ = userBindQQ;
    }

    public String getUserBindQQOpenId() {
        return userBindQQOpenId;
    }

    public void setUserBindQQOpenId(String userBindQQOpenId) {
        this.userBindQQOpenId = userBindQQOpenId;
    }

    public String getUserBindQQExpiresTime() {
        return userBindQQExpiresTime;
    }

    public void setUserBindQQExpiresTime(String userBindQQExpiresTime) {
        this.userBindQQExpiresTime = userBindQQExpiresTime;
    }

    public String getUserBindQQAccessToken() {
        return userBindQQAccessToken;
    }

    public void setUserBindQQAccessToken(String userBindQQAccessToken) {
        this.userBindQQAccessToken = userBindQQAccessToken;
    }

    public String getUserBindQQIcon() {
        return userBindQQIcon;
    }

    public void setUserBindQQIcon(String userBindQQIcon) {
        this.userBindQQIcon = userBindQQIcon;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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
