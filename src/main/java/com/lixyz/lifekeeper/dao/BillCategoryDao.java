package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface BillCategoryDao {

    /**
     * 添加账单分类
     */
    @Insert("insert into BillCategory(ObjectId,CategoryId,CategoryUser,CategoryName,IsIncome,CategoryStatus,CategoryType,CreateTime,UpdateTime,OrderIndex) values (#{billCategory.objectId},#{billCategory.categoryId},#{billCategory.categoryUser},#{billCategory.categoryName},#{billCategory.isIncome},#{billCategory.categoryStatus},#{billCategory.categoryType},#{billCategory.createTime},#{billCategory.updateTime},#{billCategory.orderIndex}) ")
    int insertBillCategory(@Param("billCategory") BillCategory billCategory) throws SQLException;


    /**
     * 删除账单分类
     */
    @Update({
            "<script>",
            "Update BillCategory set CategoryStatus = -1 ",
            "where ObjectId in",
            "<foreach collection='objectIdList' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteBillCategory(@Param("objectIdList") ArrayList<String> objectIdList) throws SQLException;

    /**
     * 根据用户ID 获取该用户下所有的账单分类
     */
    @Select("SELECT * FROM BillCategory WHERE categoryUser = #{userId} and CategoryStatus = 1 and IsIncome = #{IsIncome} order by orderIndex ASC")
    List<BillCategory> selectBillCategoryByUserId(@Param("userId") String userId, @Param("IsIncome") int IsIncome) throws SQLException;


    /**
     * 账单分类是否存在
     */
    @Select("Select count(*) from BillCategory where CategoryUser = #{userId} and CategoryName = #{categoryName} and IsIncome = #{isIncome} and CategoryStatus = 1")
    long categoryIsExists(@Param("userId") String userId, @Param("categoryName") String categoryName, @Param("isIncome") int isIncome) throws SQLException;

    /**
     * 更新账单分类
     */
    @Update("Update BillCategory set CategoryName = #{category.categoryName}, IsIncome = #{category.isIncome},OrderIndex = #{category.orderIndex},updateTime = #{updateTime} where ObjectId = #{category.objectId}")
    int updateBillCategory(@Param("category") BillCategory category, @Param("updateTime") long updateTime) throws SQLException;

    /**
     * 更新账单分类排序
     */
    @Update("Update BillCategory set OrderIndex=#{newOrderIndex} where ObjectId=#{objectId}")
    int updateBillCategoryOrderIndex(@Param("objectId") String objectId, @Param("newOrderIndex") int newOrderIndex) throws SQLException;

    /**
     * 获取账单分类最大下标
     */
    @Select("select ifnull(max(OrderIndex),0) from BillCategory where CategoryUser=#{userId}")
    long getTopIndex(@Param("userId") String userId) throws SQLException;

    @Select("Select * from BillCategory where CategoryUser = #{userId} and CategoryStatus = 1")
    ArrayList<BillCategory> getBillCategory(String userId) throws SQLException;
}
