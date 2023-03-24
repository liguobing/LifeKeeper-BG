package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.netdisk.image.ImageBean;
import com.lixyz.lifekeeper.bean.netdisk.image.ImageCategoryBean;
import com.lixyz.lifekeeper.bean.user.UserBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface ImageDao {
    @Insert("Insert into Images (ObjectId,ImageId,Sha1,FileCategory,OriginalFileName,SourceFileName,ThumbnailFileName,CoverFileName,BlurFileName,CreateTime,ImageUser,ImageStatus,ImageType,UpdateTime,MetaTime,YearMonth) " +
            "values " +
            "(#{objectId},#{imageId},#{sha1},#{fileCategory},#{originalFileName},#{sourceFileName},#{thumbnailFileName},#{coverFileName},#{blurFileName},#{createTime},#{imageUser},#{imageStatus},#{imageType},#{updateTime},#{metaTime},#{yearMonth})")
    long insertImage(@Param("objectId") String objectId,
                     @Param("imageId") String imageId,
                     @Param("sha1") String sha1,
                     @Param("fileCategory") String fileCategory,
                     @Param("originalFileName") String originalFileName,
                     @Param("sourceFileName") String sourceFileName,
                     @Param("thumbnailFileName") String thumbnailFileName,
                     @Param("coverFileName") String coverFileName,
                     @Param("blurFileName") String blurFileName,
                     @Param("createTime") long createTime,
                     @Param("imageUser") String imageUser,
                     @Param("imageStatus") int imageStatus,
                     @Param("imageType") int imageType,
                     @Param("updateTime") long updateTime,
                     @Param("metaTime") long metaTime,
                     @Param("yearMonth") String yearMonth
    ) throws SQLException;

    @Select("SELECT count(sha1) FROM Images WHERE sha1 = #{sha1} and ImageStatus = 1 and ImageUser = #{userId}")
    long queryImageSha1(@Param("sha1") String sha1, @Param("userId") String userId) throws SQLException;

    @Update({
            "<script>",
            "Update Images set ImageStatus = -1 ",
            "where ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteImageByObjectId(@Param("list") List<String> list) throws SQLException;

    @Select("SELECT count(sha1) FROM Images where ImageUser = #{userId} and ImageStatus = 1")
    long queryImageCountByUserId(@Param("userId") String userId) throws SQLException;

    @Update({
            "<script>",
            "Update Images set FileCategory = #{targetCategoryId}",
            "where ImageUser = #{userId} and ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int moveImages(@Param("targetCategoryId") String targetCategoryId, @Param("list") ArrayList<String> list, @Param("userId") String userId, @Param("updateTime") long updateTime) throws SQLException;

    @Select("select * from ImageCategory where CategoryStatus = 1 and CategoryName = #{name} and CategoryUser = #{userId}")
    ImageCategoryBean categoryNameIsExists(@Param("name") String name, @Param("userId") String userId) throws SQLException;

    @Insert("Insert into ImageCategory (ObjectId,CategoryId,CategoryName,CategoryUser,CategoryStatus,CategoryType,CreateTime,UpdateTime,IsPrivate,Password)" +
            "values " +
            "(#{bean.objectId},#{bean.categoryId},#{bean.categoryName},#{bean.categoryUser},#{bean.categoryStatus},#{bean.categoryType},#{bean.createTime},#{bean.updateTime},#{bean.isPrivate},#{bean.password})")
    long addImageCategory(@Param("bean") ImageCategoryBean bean) throws SQLException;

    @Select("Select * from ImageCategory where CategoryUser = #{userId} and CategoryStatus = 1")
    ArrayList<ImageCategoryBean> getImageCategories(@Param("userId") String userId) throws SQLException;

    @Select("Select count(1) from Images where ImageUser = #{userId} and FileCategory = #{categoryId} and ImageStatus = 1")
    int getImageCountByCategory(@Param("categoryId") String categoryId, @Param("userId") String userId) throws SQLException;

    @Select("Select count(1) from Images where ImageUser = #{userId} and ImageStatus = 1")
    int getImageCountByUserId(@Param("userId") String userId) throws SQLException;

    @Select("Select * from Images where ImageUser = #{userId} and FileCategory = #{categoryId} and ImageStatus = 1 order by CreateTime desc limit #{offset},#{rows}")
    ArrayList<ImageBean> getRangeImageByCategory(@Param("categoryId") String categoryId, @Param("userId") String userId, @Param("offset") int offset, @Param("rows") int rows) throws SQLException;

    @Select("Select categoryName from ImageCategory where categoryId = #{categoryId} and CategoryUser = #{categoryUser} and categoryStatus = 1")
    String getImageCategoryName(@Param("categoryId") String categoryId, @Param("categoryUser") String categoryUser) throws SQLException;

    @Select("select * from Images where FileCategory = #{fileCategory} and ImageStatus = 1 order by MetaTime DESC LIMIT 1")
    ImageBean getLatestImageByCategory(@Param("fileCategory") String fileCategory) throws SQLException;

    @Select("Select * from ImageCategory where CategoryName = '未分类' and CategoryStatus = 1 and CategoryUser = #{imageUser}")
    ImageCategoryBean getDefaultCategory(@Param("imageUser") String imageUser) throws SQLException;

    @Update("Update ImageCategory set objectId = #{bean.objectId},categoryName = #{bean.categoryName},categoryUser = #{bean.categoryUser},isPrivate = #{bean.isPrivate},password = #{bean.password},categoryStatus = #{bean.categoryStatus},categoryType = #{bean.categoryType},updateTime = #{bean.updateTime} where CategoryId = #{bean.categoryId}")
    int updateImageCategory(@Param("bean") ImageCategoryBean bean) throws SQLException;


    @Delete("Update ImageCategory set CategoryStatus = -1 where CategoryUser = #{userId} and CategoryId = #{categoryId}")
    int deleteImageCategory(@Param("userId") String userId, @Param("categoryId") String categoryId) throws SQLException;

    @Select("Select count(1) from ImageCategory where CategoryUser = #{userId} and CategoryId = #{categoryId} and Password = #{password} and CategoryStatus = 1")
    int checkImageCategoryPassword(@Param("userId") String userId, @Param("categoryId") String categoryId, @Param("password") String password) throws SQLException;

    @Select("select * from ImageCategory where categoryId != #{categoryId} and CategoryUser = #{userId} and CategoryStatus = 1")
    ArrayList<ImageCategoryBean> getOtherCategory(@Param("categoryId") String categoryId, @Param("userId") String userId) throws SQLException;

    @Select("Select * from User where userId = #{userId}")
    UserBean getUserByUserId(@Param("userId") String userId) throws SQLException;

    @Delete("Delete from ImageCategory where CategoryUser = #{userId} and CategoryId = #{categoryId}")
    int deleteImageCategoryByCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId) throws SQLException;

    @Select("Select * from ImageCategory where CategoryId = #{categoryId}")
    ImageCategoryBean getImageCategoryByCategoryId(@Param("categoryId") String categoryId) throws SQLException;

    @Select("Select distinct MetaTime from Images where ImageStatus = 1")
    ArrayList<Long> getTitle();

    @Select("Select distinct MetaTime from Images where ImageUser = #{userId} and ImageStatus = 1 order by MetaTime Desc")
    ArrayList<Long> getImageMetaTime(@Param("userId") String userId) throws SQLException;

    @Select("Select * from Images where ImagesUser = #{userId} and MetaTime = #{time} and ImageStatus = 1 order by createTime")
    ArrayList<ImageBean> getImagesByMetaTime(@Param("userId") String userId, @Param("time") long time);


    @Select("Select * from Images where ImageUSER = #{userId} and ImageStatus = 1 order by MetaTime Desc")
    ArrayList<ImageBean> getAllImages(@Param("userId") String userId) throws SQLException;

    @Update("Update Images set YearMonth = #{timeInMillis} where ObjectId = #{objectId}")
    void setYearMonth(@Param("objectId") String objectId, @Param("timeInMillis") long timeInMillis);

    @Update("Update Images set YearMonth = #{timeInMillis},MetaTime = #{time} where ObjectId = #{objectId}")
    void updateMeta(@Param("objectId") String objectId, @Param("time") long time, @Param("timeInMillis") long timeInMillis);

    @Select("Select distinct YearMonth from Images where fileCategory = #{fileCategory} and imageUser = #{userId} and ImageStatus = 1 order by YearMonth DESC")
    ArrayList<Long> getYearMonth(@Param("userId") String userId, @Param("fileCategory") String fileCategory) throws SQLException;

    @Select("Select * from Images where YearMonth = #{time} and ImageUser = #{userId} and ImageStatus = 1 and fileCategory = #{fileCategory} order by MetaTime")
    ArrayList<ImageBean> getImagesByYearMonth(@Param("time") long time, @Param("userId") String userId, @Param("fileCategory") String fileCategory);

    @Update("Update ImageCategory set IsPrivate = 1,Password = #{password} where CategoryId = #{categoryId}")
    int setCategoryPrivate(@Param("categoryId") String categoryId, @Param("password") String password);

    @Update("Update ImageCategory set IsPrivate = -1,Password = #{password} where CategoryId = #{categoryId}")
    int setCategoryPublic(@Param("categoryId") String categoryId, @Param("password") String password);

    @Select("Select count(ObjectId) from ImageCategory where CategoryUser =  #{userId} and CategoryId = #{categoryId} and Password = #{password}")
    int verifyImageCategoryPassword(@Param("userId") String userId, @Param("categoryId") String categoryId, @Param("password") String password) throws SQLException;

    @Select("Select * from Images where FileCategory = #{categoryId} and ImageUser = #{userId} and ImageStatus = 1")
    ArrayList<ImageBean> getImageThumbnail(@Param("userId") String userId, @Param("categoryId") String categoryId);

}
