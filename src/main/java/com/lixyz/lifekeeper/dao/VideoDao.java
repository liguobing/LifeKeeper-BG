package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.netdisk.video.VideoBean;
import com.lixyz.lifekeeper.bean.netdisk.video.VideoCategoryBean;
import com.lixyz.lifekeeper.bean.user.UserBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface VideoDao {

    @Insert("Insert into Videos (ObjectId,VideoId,Sha1,Duration,FileCategory,OriginalFilename,SourceFileName,CoverFileName,ThumbnailFileName,BlurFileName,VideoUser,VideoStatus,CreateTime,VideoWidth,VideoHeight) " +
            "values (#{objectId},#{videoId},#{sha1},#{duration},#{fileCategory},#{originalFilename},#{sourceFileName},#{coverFileName},#{thumbnailFileName},#{blurFileName},#{videoUser},#{videoStatus},#{createTime},#{width},#{height})")
    long insertVideo(
            @Param("objectId") String objectId,
            @Param("videoId") String videoId,
            @Param("sha1") String sha1,
            @Param("duration") long duration,
            @Param("fileCategory") String fileCategory,
            @Param("originalFilename") String originalFilename,
            @Param("sourceFileName") String sourceFileName,
            @Param("coverFileName") String coverFileName,
            @Param("thumbnailFileName") String thumbnailFileName,
            @Param("blurFileName") String blurFileName,
            @Param("videoUser") String videoUser,
            @Param("videoStatus") int videoStatus,
            @Param("createTime") long createTime,
            @Param("width") int width,
            @Param("height") int height
    ) throws SQLException;

    @Select("SELECT count(sha1) FROM Videos WHERE sha1 = #{sha1} and VideoStatus = 1")
    long queryVideoSha1(@Param("sha1") String sha1) throws SQLException;

    @Select("select * from Videos where FileCategory = #{fileCategory} and VideoStatus = 1 order by CreateTime DESC LIMIT 1")
    VideoBean getLatestVideoByCategory(@Param("fileCategory") String fileCategory) throws SQLException;

    @Update({
            "<script>",
            "Update Videos set VideoStatus = -1 ",
            "where ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteVideoByObjectId(@Param("list") List<String> list) throws SQLException;

    @Select("select count(ObjectId) from Videos where videoUser = #{userId} and VideoStatus = 1")
    long queryVideoCount(@Param("userId") String userId) throws SQLException;

    @Select("Select * from VideoCategory where CategoryUser = #{userId} and CategoryStatus = 1")
    ArrayList<VideoCategoryBean> getVideoCategories(@Param("userId") String userId) throws SQLException;

    @Select("Select count(1) from Videos where VideoUser = #{userId} and FileCategory = #{categoryId} and VideoStatus = 1")
    int getVideoCountByCategory(@Param("categoryId") String categoryId, @Param("userId") String userId) throws SQLException;

    @Select("Select * from Videos where VideoUser = #{userId} and FileCategory = #{categoryId} and VideoStatus = 1 order by CreateTime desc limit #{offset},#{rows}")
    ArrayList<VideoBean> getRangeVideoByCategory(@Param("categoryId") String categoryId, @Param("userId") String userId, @Param("offset") int offset, @Param("rows") int rows) throws SQLException;

    @Insert("Insert into VideoCategory (ObjectId,CategoryId,CategoryName,CategoryUser,CategoryStatus,CategoryType,CreateTime,UpdateTime,IsPrivate,Password)" +
            "values " +
            "(#{bean.objectId},#{bean.categoryId},#{bean.categoryName},#{bean.categoryUser},#{bean.categoryStatus},#{bean.categoryType},#{bean.createTime},#{bean.updateTime},#{bean.isPrivate},#{bean.password})")
    long addVideoCategory(@Param("bean") VideoCategoryBean bean) throws SQLException;

    @Select("Select categoryName from VideoCategory where categoryId = #{categoryId} and CategoryUser = #{categoryUser} and categoryStatus = 1")
    String getVideoCategoryName(@Param("categoryId") String categoryId, @Param("categoryUser") String categoryUser) throws SQLException;

    @Update({
            "<script>",
            "Update Videos set FileCategory = #{targetCategoryId}",
            "where VideoUser = #{userId} and ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int moveVideo(@Param("targetCategoryId") String targetCategoryId, @Param("list") ArrayList<String> list, @Param("userId") String userId, @Param("updateTime") long updateTime) throws SQLException;


    @Select("Select * from VideoCategory where CategoryStatus = 1 and CategoryUser = #{videoUser} and CategoryName = '未分类'")
    VideoCategoryBean getDefaultCategoryByName(@Param("videoUser") String videoUser) throws SQLException;

    @Select("Select count(1) from VideoCategory where CategoryStatus = 1 and CategoryUser = #{userId} and CategoryId = #{categoryId} and Password = #{password}")
    int checkVideoCategoryPassword(@Param("categoryId") String categoryId, @Param("password") String password, @Param("userId") String userId) throws SQLException;

    @Select("select * from VideoCategory where categoryId != #{categoryId} and CategoryUser = #{userId} and CategoryStatus = 1")
    ArrayList<VideoCategoryBean> getOtherCategory(@Param("categoryId") String categoryId, @Param("userId") String userId) throws SQLException;

    @Update("Update VideoCategory set objectId = #{bean.objectId},categoryName = #{bean.categoryName},categoryUser = #{bean.categoryUser},isPrivate = #{bean.isPrivate},password = #{bean.password},categoryStatus = #{bean.categoryStatus},categoryType = #{bean.categoryType},updateTime = #{bean.updateTime} where CategoryId = #{bean.categoryId}")
    int updateVideoCategory(@Param("bean") VideoCategoryBean bean) throws SQLException;

    @Select("Select * from VideoCategory where CategoryName = #{categoryName} and CategoryUser = #{categoryUser}")
    VideoCategoryBean videoCategoryNameIsExists(@Param("categoryName") String categoryName, @Param("categoryUser") String categoryUser) throws SQLException;

    @Select("Select * from VideoCategory where CategoryId = #{categoryId}")
    VideoCategoryBean getVideoCategoryByCategoryId(@Param("categoryId") String categoryId) throws SQLException;

    @Select("Select * from User where userId = #{userId}")
    UserBean getUserByUserId(String userId) throws SQLException;

    @Update("Update VideoCategory set CategoryStatus = -1 where CategoryUser = #{userId} and CategoryId = #{categoryId}")
    int deleteVideoCategory(String userId, String categoryId) throws SQLException;

    @Select("Select * from VideoCategory where CategoryName = '未分类' and CategoryStatus = 1 and CategoryUser = #{imageUser}")
    VideoCategoryBean getDefaultCategory(@Param("imageUser") String userId) throws SQLException;

    @Select("Select count(1) from Videos where VideoUser = #{userId} and VideoStatus = 1")
    int getVideoCountByUserId(@Param("userId") String userId) throws SQLException;

    @Update("Update VideoCategory set IsPrivate = 1,Password = #{password} where CategoryId = #{categoryId}")
    int setCategoryPrivate(@Param("categoryId") String categoryId, @Param("password") String password);

    @Update("Update VideoCategory set IsPrivate = -1,Password = #{password} where CategoryId = #{categoryId}")
    int setCategoryPublic(@Param("categoryId") String categoryId, @Param("password") String password);

    @Select("Select * from Videos where FileCategory = #{categoryId} and VideoUser = #{userId} and VideoStatus = 1")
    ArrayList<VideoBean> getVideoThumbnail(@Param("userId")String userId, @Param("categoryId")String categoryId);
}
