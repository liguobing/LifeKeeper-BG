package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.netdisk.image.ImageBean;
import com.lixyz.lifekeeper.bean.netdisk.record.RecordBean;
import com.lixyz.lifekeeper.bean.netdisk.video.VideoBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;

@Component
@Mapper
public interface NetDiskDao {

    @Select("Select count(*) from Images where ImageUser=#{userId} and ImageStatus = 1")
    int getImageCount(@Param("userId") String userId) throws SQLException;

    @Select("Select count(*) from PhoneRecord where RecordUser=#{userId}  and RecordStatus = 1 ")
    int getRecordCount(@Param("userId") String userId) throws SQLException;

    @Select("Select count(*) from Videos where VideoUser=#{userId} and VideoStatus = 1")
    int getVideoCount(@Param("userId") String userId) throws SQLException;

    @Select("Select distinct FileCategory from Images where ImageUser=#{userId} and ImageStatus = 1")
    ArrayList<String> getImageCategory(@Param("userId") String userId) throws SQLException;

    @Select("Select * from Images where ImageUser=#{userId} and FileCategory=#{categoryName} and ImageStatus = 1")
    ArrayList<ImageBean> getImageByCategory(@Param("userId") String userId, @Param("categoryName") String categoryName) throws SQLException;

    @Select("select * from PhoneRecord where RecordUser = #{userId}  and RecordStatus = 1 order by CallTime DESC,ContactName ASC")
    ArrayList<RecordBean> getRecordOrderByTime(@Param("userId") String userId) throws SQLException;

    @Select("select * from PhoneRecord where RecordUser = #{userId} and RecordStatus = 1  order by ContactName ASC ,CallTime DESC")
    ArrayList<RecordBean> getRecordOrderByName(@Param("userId") String userId) throws SQLException;

    @Select("Select * from Videos where VideoUser=#{userId} and VideoStatus = 1")
    ArrayList<VideoBean> getVideos(@Param("userId") String userId) throws SQLException;
}
