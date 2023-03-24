package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.user.UserBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Mapper
public interface LifeKeeperDao {

    @Select("SELECT ifnull(SUM(LastUpdateTime),0) FROM LastUpdateTime and UserId = #{userId}")
    long getLastUpdateTime(@Param("userId") String userId) throws SQLException;

    @Select("SELECT UserName FROM User WHERE UserId = #{userId}  and UserStatus = 1")
    String getUserName(@Param("userId") String userId) throws SQLException;

    @Select("SELECT UserIconUrl FROM User WHERE UserId = #{userId}  and UserStatus = 1")
    String getUserIconUrl(@Param("userId") String userId) throws SQLException;

    @Select("SELECT * FROM User WHERE UserPhone = #{phone}  and UserStatus = 1")
    UserBean getUserByPhone(@Param("phone") String phone) ;

    @Select("SELECT * FROM User WHERE UserId = #{userId} and UserStatus = 1")
    UserBean getUserByUserId(@Param("userId") String userId) ;
}

