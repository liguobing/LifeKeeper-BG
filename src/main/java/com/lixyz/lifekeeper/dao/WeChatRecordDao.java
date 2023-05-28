package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.netdisk.record.ContactBean;
import com.lixyz.lifekeeper.bean.netdisk.record.RecordBean;
import com.lixyz.lifekeeper.bean.netdisk.record.WeChatRecordBean;
import com.lixyz.lifekeeper.bean.netdisk.record.WeChatRecordContactBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;

@Component
@Mapper
public interface WeChatRecordDao {
    @Select("Select ContactId from WeChatRecordContact where ContactStatus = 1 and ContactName = #{contactName} and ContactUser = #{recordUser} limit 1")
    String getWeChatContactIdByContactName(@Param("contactName") String contactName, @Param("recordUser") String userId) throws SQLException;

    @Insert("Insert into WeChatRecordContact(ObjectId,ContactId,ContactName,ContactUser,ContactStatus) values (#{bean.objectId},#{bean.contactId},#{bean.contactName},#{bean.contactUser},#{bean.contactStatus})")
    void insertWeChatContact(@Param("bean") WeChatRecordContactBean contactBean) throws SQLException;

    @Select("Select * from WeChatRecordContact where ContactStatus = 1 and ContactName = #{contactName} and ContactUser = #{userId}")
    String getContactIdByContactName(@Param("contactName") String contactName, @Param("userId") String userId) throws SQLException;

    @Select("Select count(ObjectId) from WeChatRecord where Sha1 = #{sha1}")
    int fileIsExists(@Param("sha1") String sha1) throws SQLException;

    /**
     * 插入录音
     */
    @Insert("Insert into WeChatRecord (ObjectId,RecordId,Sha1,ContactId,CallTime,SourceFileName,OriginalFileName,RecordUser,RecordStatus,CreateTime) " +
            "values (#{record.objectId},#{record.recordId},#{record.sha1},#{record.contactId},#{record.callTime},#{record.sourceFileName},#{record.originalFileName},#{record.recordUser},#{record.recordStatus},#{record.createTime})")
    void insertWeChatRecord(@Param("record") WeChatRecordBean recordBean) throws SQLException;

    /**
     * 删除通话录音（RecordStatus = -1）
     */
    @Update({
            "<script>",
            "Update WeChatRecord set RecordStatus = -1",
            "where ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteWeChatRecordById(@Param("list") ArrayList<String> objectIds) throws SQLException;

    @Select("select count(ObjectId) from WeChatRecord where RecordUser = #{userId} and RecordStatus = 1")
    long queryWeChatRecordCountByUserId(@Param("userId") String userId) throws SQLException;

    @Select("select * from WeChatRecordContact where ContactStatus = 1 and  ContactUser = #{userId}  group by ContactId order by ContactName ASC")
    ArrayList<WeChatRecordContactBean> queryWeChatContactName(@Param("userId") String userId);

    @Select("Select * from WeChatRecord where RecordUser = #{userId} and ContactId = #{contactId} and RecordStatus = 1 order by CallTime DESC")
    ArrayList<WeChatRecordBean> queryWeChatRecordByContactId(@Param("contactId") String contactId, @Param("userId") String userId);

    @Select("Select * from WeChatRecord where RecordStatus = 1 and RecordUser = #{userId} and ContactId = #{contactId} order by callTime desc")
    ArrayList<WeChatRecordBean> getWeChatRecordByContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    @Update({
            "<script>",
            "Update WeChatRecordContact set ContactStatus = -1 ",
            "where ContactUser = #{userId} and  ContactId in",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteWeChatContact(@Param("userId") String userId, @Param("ids") ArrayList<String> contactIds);
}
