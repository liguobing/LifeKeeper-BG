package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.bill.billshop.BillShop;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface BillShopDao {

    /**
     * 新增商家
     */
    @Insert("insert into BillShop(objectId,shopId,shopName,shopIcon,shopUser,shopStatus,shopType,createTime,updateTime) " +
            " values " +
            "(#{billShop.objectId},#{billShop.shopId},#{billShop.shopName},#{billShop.shopIcon},#{billShop.shopUser},#{billShop.shopStatus},#{billShop.shopType},#{billShop.createTime},#{billShop.updateTime}) "
    )
    int insertBillShop(@Param("billShop") BillShop billShop) throws SQLException;

    /**
     * 删除商家
     */
    @Update({
            "<script>",
            "Update BillShop set ShopStatus = -1 ",
            "where ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteBillShop(@Param("list") ArrayList<String> list) throws SQLException;

    /**
     * 根据用户 ID 获取商家
     */
    @Select("SELECT * FROM BillShop WHERE shopUser = #{userId}")
    List<BillShop> selectBillShopByUserId(@Param("userId") String userId) throws SQLException;

    @Select("Select count(*) from BillShop where ShopUser = #{userId} and ShopName = #{shopName} and ShopStatus = 1")
    int billShopIsExists(@Param("userId") String userId, @Param("shopName") String shopName) throws SQLException;

    /**
     * 获取经常使用的商家
     */
    @Select("select distinct BillShop from Bill where BillStatus = 1 and BillUser = #{userId} and BillShop is not null limit #{offset},#{rows}")
    ArrayList<String> getOftenUseBillShop(@Param("userId") String userId, @Param("offset") int offset, @Param("rows") int rows) throws SQLException;

    /**
     * 获取账单商家总数
     */
    @Select("select count(*) from Bill where BillUser=#{userId} and BillShop is not null and length(BillShop)>0 and BillStatus = 1")
    int getBillShopCount(@Param("userId") String userId) throws SQLException;

    /**
     * 获取所有自定义商家
     */
    @Select("select distinct ShopName from BillShop where ShopStatus = 1 and ShopUser = #{userId} limit #{offset},#{rows}")
    ArrayList<String> getCustomShop(@Param("userId") String userId, @Param("offset") int offset, @Param("rows") int rows) throws SQLException;

    /**
     * 获取自定义商家总数
     */
    @Select("select count(*) from BillShop where ShopUser=#{userId} and ShopStatus = 1")
    int getCustomShopCount(@Param("userId") String userId) throws SQLException;

    @Update("Update BillShop set ShopName = #{shop.shopName} where ObjectId = #{shop.objectId}")
    int updateShop(@Param("shop") BillShop shop) throws SQLException;

    @Select("Select * from BillShop where ShopName = #{shopName} and ShopUser = #{shopUser}")
    BillShop shopNameIsExists(@Param("shopName") String shopName, @Param("shopUser") String shopUser) throws SQLException;

    @Select("Select * from BillShop where ShopUser = #{userId} and ShopStatus = 1")
    ArrayList<BillShop> getBillShop(String userId);
}
