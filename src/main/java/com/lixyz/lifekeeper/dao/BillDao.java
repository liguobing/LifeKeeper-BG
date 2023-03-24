package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.bill.bill.Bill;
import com.lixyz.lifekeeper.bean.bill.bill.BillImageBean;
import com.lixyz.lifekeeper.bean.bill.bill.BillMoneyCountForCategoryGroup;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.bean.netdisk.image.ImageBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface BillDao {

    /**
     * 插入账单
     */
    @Insert(
            "insert into Bill(objectId,billId,billDate,billMoney,billProperty,billCategory,billAccount,billRemark,billUser,billShop,billStatus,billType,createTime,updateTime) " +
                    " values " +
                    "(#{bill.objectId},#{bill.billId},#{bill.billDate},#{bill.billMoney},#{bill.billProperty},#{bill.billCategory},#{bill.billAccount},#{bill.billRemark},#{bill.billUser},#{bill.billShop},#{bill.billStatus},#{bill.billType},#{bill.createTime},#{bill.updateTime}) "
    )
    int insertBill(@Param("bill") Bill bill) throws SQLException;

    /**
     * 删除账单
     */
    @Update("Delete from Bill where ObjectId = #{objectId}")
    int deleteBill(@Param("objectId") String objectId) throws SQLException;

    /**
     * 查询时间范围内的收入总金额
     */
    @Select("SELECT ifnull(sum(BillMoney),0) FROM Bill WHERE billUser = #{userId} and BillStatus = 1 and BillProperty = 1 and BillDate >= #{start} and BillDate <= #{end}")
    double getIncomeCount(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    /**
     * 查询时间范围内的支出总金额
     */
    @Select("SELECT ifnull(sum(BillMoney),0) FROM Bill WHERE billUser = #{userId} and BillStatus = 1 and BillProperty = -1 and BillDate >= #{start} and BillDate <= #{end}")
    double getExpendCount(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    /**
     * 查看时间范围内的账单
     */
    @Select("SELECT * FROM Bill WHERE billUser = #{userId} and BillStatus = 1 and BillDate >= #{start} and BillDate <= #{end} Order by BillDate DESC")
    List<Bill> selectBillByMonth(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

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

    @Select({"<script>",
            "SELECT ifnull(sum(BillMoney),0) FROM Bill" +
                    " WHERE 1=1 AND BillStatus = 1 AND <![CDATA[BillDate >= #{start}]]> AND <![CDATA[BillDate <= #{end}]]> AND BillUser = #{userId}" +
                    " <if test='property!=0'>" +
                    " AND BillProperty = #{property}" +
                    " </if>" +
                    " <if test='category!=\"-1\"'>" +
                    " AND BillCategory = #{category}" +
                    " </if>" +
                    " <if test='account!=\"-1\"'>" +
                    " And BillAccount = #{account}" +
                    " </if>" +
                    " </script>"})
    double getRangeMoneyCount(@Param("start") long start, @Param("end") long end, @Param("property") int property, @Param("category") String category, @Param("account") String account, @Param("userId") String userId) throws SQLException;

    @Select({"<script>",
            "SELECT ifnull(sum(BillMoney),0) as MoneyCount,BillCategory,BillStatus as IsIncome FROM Bill" +
                    " WHERE 1=1 AND BillStatus = 1 AND <![CDATA[BillDate >= #{startTime}]]> AND <![CDATA[BillDate <= #{endTime}]]> AND BillUser = #{userId}" +
                    " <if test='property!=0'>" +
                    " AND BillProperty = #{property}" +
                    " </if>" +
                    " GROUP BY BillCategory" +
                    " </script>"})
    ArrayList<BillMoneyCountForCategoryGroup> getCategoryMoneyCount(@Param("startTime") long startTime, @Param("endTime") long endTime, @Param("userId") String userId, @Param("property") int property) throws SQLException;

    @Select("SELECT CategoryName from BillCategory where CategoryUser = #{userId} and CategoryId = #{categoryId} and CategoryStatus = 1")
    String getCategoryNameByCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId) throws SQLException;

    @Select("SELECT ifnull(sum(BillMoney),0) FROM Bill WHERE billUser = #{userId} and BillStatus = 1 and BillProperty = #{billProperty} and BillDate >= #{start} and BillDate <= #{end}")
    double getMoneyCount(@Param("start") long start, @Param("end") long end, @Param("userId") String userId, @Param("billProperty") int billProperty) throws SQLException;

    @Select("SELECT * FROM BillCategory WHERE categoryUser = #{userId} and CategoryStatus = 1  order by OrderIndex ASC")
    ArrayList<BillCategory> getBillCategories(@Param("userId") String userId) throws SQLException;

    @Select("SELECT * FROM BillCategory WHERE categoryUser = #{userId}  order by OrderIndex ASC")
    ArrayList<BillCategory> getAllBillCategories(@Param("userId") String userId) throws SQLException;


    @Select("SELECT * from Bill where BillProperty = #{property} and BillDate >= #{startTime} and BillDate <= #{endTime} AND BillStatus = 1 ORDER BY BillMoney DESC LIMIT 10")
    ArrayList<Bill> getTop10Bills(@Param("startTime") long startTime, @Param("endTime") long endTime, @Param("userId") String userId, @Param("property") int property) throws SQLException;

    @Select({"<script>",
            "SELECT ifnull(sum(BillMoney),0) as MoneyCount,BillCategory,BillStatus as IsIncome FROM Bill" +
                    " WHERE 1=1 And BillStatus = 1 AND <![CDATA[BillDate >= #{startTime}]]> AND <![CDATA[BillDate <= #{endTime}]]> AND BillUser = #{userId}" +
                    " <if test='property!=0'>" +
                    " AND BillProperty = #{property}" +
                    " </if>" +
                    " GROUP BY BillCategory ORDER BY MoneyCount DESC LIMIT 10" +
                    " </script>"})
    ArrayList<BillMoneyCountForCategoryGroup> getTop10Category(@Param("startTime") long startTime, @Param("endTime") long endTime, @Param("userId") String userId, @Param("property") int property) throws SQLException;

    @Select("Select * from Bill where BillDate >= #{start} and BillDate <= #{end} and BillStatus = 1 and BillUser=#{userId} order by ${sortName} ${sortOrder} Limit #{offset},#{rows}")
    ArrayList<Bill> getRangeBills(@Param("userId") String userId, @Param("start") long start, @Param("end") long end, @Param("offset") int offset, @Param("rows") long rows, @Param("sortName") String sortName, @Param("sortOrder") String sortOrder) throws SQLException;

    @Select("select ImageWebpName from BillImage where BillId = #{billId}")
    ArrayList<String> getBillImage(@Param("billId") String billId) throws SQLException;

    @Select("Select count(1) from Bill where BillDate >= #{start} and BillDate <= #{end} and BillStatus = 1 and BillUser=#{userId}")
    int getRangeBillCount(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    @Select("SELECT * FROM BillAccount WHERE accountUser = #{userId} and AccountStatus = 1 order by OrderIndex ASC")
    ArrayList<BillAccount> getBillAccounts(@Param("userId") String userId) throws SQLException;

    @Insert("<script>" +
            "insert into BillImage(ObjectId,ImageId,BillId,ImageSourceName,ImageCoverName,ImageThumbnailName,ImageWebpName,ImageUser) " +
            " values " +
            "<foreach collection ='list' item='item' index= 'index' separator =','> " +
            "(#{item.objectId},#{item.imageId},#{item.billId},#{item.imageSourceName},#{item.imageCoverName},#{item.imageThumbnailName},#{item.imageWebpName},#{item.imageUser})" +
            "</foreach > " +
            "</script>")
    int addBillImage(@Param("list") ArrayList<BillImageBean> images) throws SQLException;

    @Select("Select distinct categoryId from BillCategory where CategoryUser = #{userId} and CategoryName = #{categoryName} and IsIncome = #{isIncome}")
    String getBillCategoryByCategoryName(@Param("userId") String userId, @Param("categoryName") String categoryName, @Param("isIncome") int isIncome) throws SQLException;

    @Select("Select * from Bill where BillProperty = #{billProperty} and BillCategory = #{categoryId} and BillDate >= #{beginOfMonth} and BillDate <= #{endOfMonth}")
    ArrayList<Bill> getBillsByCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId, @Param("billProperty") int billProperty, @Param("beginOfMonth") long beginOfMonth, @Param("endOfMonth") long endOfMonth) throws SQLException;

    @Select("Select * from Bill where BillUser = #{id} and BillStatus = 1 and BillDate >= #{start} and BillDate <= #{end} order by BillDate Desc")
    ArrayList<Bill> getBillsByTimeRange(@Param("id") String userId, @Param("start") long dayStart, @Param("end") long dayEnd);

    @Select("Select categoryName from BillCategory where CategoryUser = #{id} and CategoryId = #{categoryId} order by CreateTime desc limit 1")
    String getBillCategoryName(@Param("id") String userId, @Param("categoryId") String billCategory);

    @Select("Select accountName from BillAccount where AccountUser = #{id} and AccountId = #{accountId} order by CreateTime desc limit 1")
    String getBillAccountName(@Param("id") String userId, @Param("accountId") String billAccount);

    @Select("Select ifnull(sum(BillMoney),0) from Bill where BillUser = #{userId} and BillStatus = 1 and BillDate >= #{startMonth} and BillDate <= #{endMonth} and BillProperty > 0")
    double getIncomeCountForMonth(@Param("userId") String userId, @Param("startMonth") long startMonth, @Param("endMonth") long endMonth);

    @Select("Select ifnull(sum(BillMoney),0) from Bill where BillUser = #{userId} and BillStatus = 1 and BillDate >= #{startMonth} and BillDate <= #{endMonth} and BillProperty < 0")
    double getExpendCountForMonth(@Param("userId") String userId, @Param("startMonth") long startMonth, @Param("endMonth") long endMonth);

    @Select("Select categoryId from BillCategory where CategoryUser = #{userId} and CategoryName = #{categoryName}")
    String getBillCategoryId(@Param("userId") String userId, @Param("categoryName") String categoryName);

    @Insert("insert into BillCategory(ObjectId,CategoryId,CategoryUser,CategoryName,IsIncome,CategoryStatus,CategoryType,CreateTime,UpdateTime,OrderIndex) values (#{billCategory.objectId},#{billCategory.categoryId},#{billCategory.categoryUser},#{billCategory.categoryName},#{billCategory.isIncome},#{billCategory.categoryStatus},#{billCategory.categoryType},#{billCategory.createTime},#{billCategory.updateTime},#{billCategory.orderIndex}) ")
    void addBillCategory(@Param("billCategory") BillCategory category);

    @Select("Select accountId from BillAccount where AccountUser = #{userId} and AccountName = #{accountName}")
    String getBillAccountId(@Param("userId") String userId, @Param("accountName") String accountName) throws SQLException;

    @Insert("Insert into BillAccount (ObjectId,AccountId,AccountUser,AccountName,AccountStatus,AccountType ,CreateTime ,UpdateTime,OrderIndex) values (#{billAccount.objectId},#{billAccount.accountId},#{billAccount.accountUser},#{billAccount.accountName},#{billAccount.accountStatus},#{billAccount.accountType},#{billAccount.createTime},#{billAccount.updateTime},#{billAccount.orderIndex})")
    int addBillAccount(@Param("billAccount") BillAccount billAccount) throws SQLException;

    @Select("Select count(*) from Bill where ObjectID = #{objectId}")
    int billIsImported(@Param("objectId") String objectId) throws SQLException;
}