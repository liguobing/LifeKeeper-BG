package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.bean.sms.SMSBean;
import com.lixyz.lifekeeper.bean.user.UserBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
@Mapper
public interface UserDao {

    /**
     * 插入用户
     *
     * @param user 用户
     * @return 是否插入成功
     */
    @Insert("Insert into User(ObjectId,UserId,UserName,UserPhone,UserPassword,UserIconUrl,UserBindWeibo,UserBindWeiboAccessToken,UserBindWeiboIcon,UserBindWeiboExpiresTime,UserBindWeiboId,UserBindQQ,UserBindQQOpenId,UserBindQQExpiresTime,UserBindQQAccessToken,UserBindQQIcon,UserStatus,UserType,CreateTime,UpdateTime)values"
            +
            "(#{user.objectId},#{user.userId},#{user.userName},#{user.userPhone},#{user.userPassword},#{user.userIconUrl},#{user.userBindWeibo},#{user.userBindWeiboAccessToken},#{user.userBindWeiboIcon},#{user.userBindWeiboExpiresTime},#{user.userBindWeiboId},#{user.userBindQQ},#{user.userBindQQOpenId},#{user.userBindQQExpiresTime},#{user.userBindQQAccessToken},#{user.userBindQQIcon},#{user.userStatus},#{user.userType},#{user.createTime},#{user.updateTime})")
    int insertUser(@Param("user") UserBean user) throws SQLException;

    /**
     * 添加账单分类
     *
     * @param billCategoryList 账单分类列表
     * @return 是否添加成功
     */
    @Insert("<script>" +
            "insert into BillCategory(ObjectId,CategoryId,CategoryUser,CategoryName,IsIncome,CategoryStatus,CategoryType,CreateTime,UpdateTime,OrderIndex) " +
            " values " +
            "<foreach collection ='billCategoryList' item='item' index= 'index' separator =','> " +
            "(#{item.objectId},#{item.categoryId},#{item.categoryUser},#{item.categoryName},#{item.isIncome},#{item.categoryStatus},#{item.categoryType},#{item.createTime},#{item.updateTime},#{item.orderIndex}) " +
            "</foreach > " +
            "</script>")
    int addBillCategory(@Param("billCategoryList") List<BillCategory> billCategoryList) throws SQLException;

    /**
     * 插入账单账户
     *
     * @param billAccountList 要插入的数据 List
     * @return 插入成功个数
     */
    @Insert("<script>" +
            "insert into BillAccount(ObjectId,AccountId,AccountUser,AccountName,AccountStatus,AccountType ,CreateTime ,UpdateTime,OrderIndex) " +
            " values " +
            "<foreach collection ='billAccountList' item='item' index= 'index' separator =','> " +
            "(#{item.objectId},#{item.accountId},#{item.accountUser},#{item.accountName},#{item.accountStatus},#{item.accountType},#{item.createTime},#{item.updateTime},#{item.orderIndex}) " +
            "</foreach > " +
            "</script>")
    int addBillAccount(@Param("billAccountList") List<BillAccount> billAccountList) throws SQLException;

    /**
     * 删除用户
     *
     * @param objectId   旧用户 ObjectId
     * @param updateTime 旧用户更新时间
     * @return 删除用户成功个数
     */
    @Update("Update User set userStatus = -1 , userType = 1,updateTime=#{updateTime} where objectId=#{objectId}")
    int deleteUser(@Param("objectId") String objectId, @Param("updateTime") long updateTime) throws SQLException;


    /**
     * 根据用户手机和密码查找用户
     *
     * @param userPhone    用户手机号
     * @param userPassword 用户密码
     * @return 符合的用户列表
     */
    @Select("select * from `User` where UserPhone = #{userPhone} and UserPassword = #{userPassword} and UserStatus = 1 ORDER BY CreateTime DESC LIMIT 1")
    UserBean selectUserByPhoneAndPassword(@Param("userPhone") String userPhone, @Param("userPassword") String userPassword) throws SQLException;

    @Select("SELECT count(*) FROM User WHERE userStatus =  1 and userPhone = #{phone}")
    long phoneIsRegistered(@Param("phone") String phone) throws SQLException;


    /**
     * 根据 weibo id 查找用户
     *
     * @param userBindWeiboId weibo id
     * @return 用户数据列表
     */
    @Select("Select * from User where userBindWeiboId = #{userBindWeiboId} and UserStatus = 1 order by UpdateTime ASC LIMIT 1")
    UserBean selectUserByWeiboId(@Param("userBindWeiboId") String userBindWeiboId) throws SQLException;

    /**
     * 根据 QQ id 查找用户
     *
     * @param userBindQQId QQ id
     * @return 用户数据列表
     */
    @Select("Select * from User where userBindQQOpenId = #{userBindQQId} and UserStatus = 1 order by UpdateTime,CreateTime LIMIT 1")
    UserBean selectUserByQQId(@Param("userBindQQId") String userBindQQId) throws SQLException;


    /**
     * 更新用户
     *
     * @param updateTime 旧用户更新时间
     * @param objectId   旧用户 ObjectId
     * @return 更新成功个数
     */
    @Update("Update User set userStatus = -1 , userType=2 , updateTime = #{updateTime} where ObjectId = #{objectId}")
    int updateUser(@Param("updateTime") long updateTime, @Param("objectId") String objectId) throws SQLException;


    @Select("Select * from User where UserPhone = #{phone} and UserStatus = 1 order by UpdateTime ASC LIMIT 1")
    UserBean selectUserByPhone(@Param("phone") String phone) throws SQLException;

    @Select("select * from User where UserPhone = #{phone} and UserBindWeibo is not null")
    UserBean checkPhoneBindWeibo(@Param("phone") String phone) throws SQLException;

    @Select("select * from User where UserPhone = #{phone} and UserBindQQ is not null")
    UserBean checkPhoneBindQQ(@Param("phone") String phone) throws SQLException;

    /**
     * 查询时间范围内的收入总和
     */
    @Select("Select ifnull(sum(BillMoney),0) from Bill where BillUser=#{userId} and BillStatus = 1 and BillProperty = 1 and BillDate >= #{start} and BillDate <= #{end}")
    double getCurrentMonthIncomeCount(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    /**
     * 查询时间范围内的支出总和
     */
    @Select("Select ifnull(sum(BillMoney),0) from Bill where BillUser=#{userId} and BillStatus = 1 and BillProperty = -1 and BillDate >= #{start} and BillDate <= #{end}")
    double getCurrentMonthExpendCount(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    /**
     * 获取固定时间范围内的计划总数
     */
    @Select("Select count(*) from Plan where PlanUser = #{userId} and PlanStatus = 1 and StartTime >= #{dayStart} and StartTime <= #{dayEnd}")
    int getPlayCount(@Param("userId") String userId, @Param("dayStart") long dayStart, @Param("dayEnd") long dayEnd) throws SQLException;

    @Select("SELECT count(sha1) FROM Images where ImageUser = #{userId} and ImageStatus = 1")
    long queryImageCountByUserId(@Param("userId") String userId) throws SQLException;

    /**
     * 获取某个用户的录音总数
     */
    @Select("select count(ObjectId) from PhoneRecord where RecordUser = #{userId} and RecordStatus = 1")
    long queryRecordCountByUserId(@Param("userId") String userId) throws SQLException;

    @Select("select count(ObjectId) from Videos where videoUser = #{userId} and VideoStatus = 1")
    long queryVideoCount(@Param("userId") String userId) throws SQLException;

    @Update("Update User set UserIconUrl = #{name} where UserId = #{userId}")
    int updateUserIcon(@Param("userId") String userId, @Param("name") String name);

    @Update("Update PushClient set ClientId = #{clientId} where UserId = #{userId}")
    int resetClientId(@Param("userId") String userId, @Param("clientId") String clientId);

    @Insert("Insert into PushClient(UserId,ClientId)values"
            +
            "(#{userId},#{clientId})")
    int addPushClient(@Param("userId") String userId, @Param("clientId") String clientId);

    @Select("Select * from User where UserId = #{userId} and UserPhone = #{phone} and UserStatus = 1")
    UserBean checkUserIsExists(@Param("userId") String userId, @Param("phone") String phone);


    @Insert("Insert into SMS(ObjectId,SMSId,PhoneNumber,SMSCode,SMSStatus,SMSType,CreateTime,VerifyTime,PositionNum,UserId)values"
            +
            "(#{bean.objectId},#{bean.sMSId},#{bean.phoneNumber},#{bean.sMSCode},#{bean.sMSStatus},#{bean.sMSType},#{bean.createTime},#{bean.verifyTime},#{bean.positionNum},#{bean.userId})")
    int addSmsCode(@Param("bean") SMSBean bean);

    @Select("Select count(*) from SMS where PhoneNumber = #{phone} and VerifyTime = 0 and SMSCode = #{code} and PositionNum = #{codeStamp}")
    int verifySMSCode(@Param("phone") String phone, @Param("code") String code, @Param("codeStamp") String codeStamp);

    @Update("Update SMS set VerifyTime = #{time} where PhoneNumber = #{phone} and  SMSCode = #{code} and PositionNum = #{codeStamp}")
    int failSMSCode(@Param("phone") String phone, @Param("code") String code, @Param("codeStamp") String codeStamp, @Param("time") long time);

    @Update("Update User set UserPhone = #{phone} where UserId = #{userId}")
    int resetPhone(@Param("userId") String userId, @Param("phone") String phone);

    @Select("Select * from User where UserId = #{userId} and UserStatus = 1")
    UserBean getUserByUserId(@Param("userId") String userId);

    @Update("Update User set UserPassword = #{password} where UserPhone = #{phone} and UserStatus = 1")
    int resetPassword(@Param("phone") String phone, @Param("password") String password) throws SQLException;
}