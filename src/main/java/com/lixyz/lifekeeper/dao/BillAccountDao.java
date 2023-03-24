package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface BillAccountDao {

    /**
     * 插入账单账户
     */
    @Insert("Insert into BillAccount (ObjectId,AccountId,AccountUser,AccountName,AccountStatus,AccountType ,CreateTime ,UpdateTime,OrderIndex) values (#{billAccount.objectId},#{billAccount.accountId},#{billAccount.accountUser},#{billAccount.accountName},#{billAccount.accountStatus},#{billAccount.accountType},#{billAccount.createTime},#{billAccount.updateTime},#{billAccount.orderIndex})")
    int insertBillAccount(@Param("billAccount") BillAccount billAccount) throws SQLException;


    /**
     * 删除账单账户
     */
    @Update({
            "<script>",
            "Update BillAccount set AccountStatus = -1 ",
            "where ObjectId in",
            "<foreach collection='objectIdList' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteBillAccount(@Param("objectIdList") ArrayList<String> objectIdList) throws SQLException;

    /**
     * 根据用户 ID 搜索账单账户
     */
    @Select("SELECT * FROM BillAccount WHERE accountUser = #{userId} and accountStatus = 1")
    List<BillAccount> selectBillAccountByUserId(@Param("userId") String userId) throws SQLException;

    /**
     * 账单账户是否存在
     */
    @Select("Select count(*) from BillAccount where AccountUser = #{userId} and AccountName = #{accountName} and AccountStatus = 1")
    long accountIsExists(@Param("userId") String userId, @Param("accountName") String accountName) throws SQLException;

    /**
     * 更新账单账户
     */
    @Update("Update BillAccount set accountName = #{account.accountName},orderIndex = #{account.orderIndex} ,updateTime = #{updateTime} where ObjectId = #{account.objectId}")
    int updateBillAccount(@Param("account") BillAccount account, @Param("updateTime") long updateTime) throws SQLException;

    /**
     * 更新账单账户排序
     */
    @Update("Update BillAccount set OrderIndex=#{orderIndex} where ObjectId=#{objectId}")
    int updateBillAccountOrder(@Param("objectId") String objectId, @Param("orderIndex") int orderIndex) throws SQLException;

    /**
     * 获取最大的排序下标
     */
    @Select("select ifnull(max(OrderIndex),0) from BillAccount where AccountUser=#{userId}")
    long getTopIndex(@Param("userId") String userId) throws SQLException;

    @Select("Select ifnull(sum(BillMoney),0) from Bill where BillUser=#{userId} and BillStatus = 1 and BillProperty = 1 and BillDate >= #{monthStart} and BillDate <= #{monthEnd}")
    Double getCurrentMonthIncomeCount(@Param("userId") String userId, @Param("monthStart") long monthStart, @Param("monthEnd") long monthEnd) throws SQLException;


    @Select("Select ifnull(sum(BillMoney),0) from Bill where BillUser=#{userId} and BillStatus = 1 and BillProperty = -1 and BillDate >= #{monthStart} and BillDate <= #{monthEnd}")
    Double getCurrentMonthExpendCount(@Param("userId") String userId, @Param("monthStart") long monthStart, @Param("monthEnd") long monthEnd) throws SQLException;

    @Select("Select * from BillAccount where AccountUser = #{userId} and AccountStatus = 1")
    ArrayList<BillAccount> getBillAccount(String userId)throws SQLException;
}
