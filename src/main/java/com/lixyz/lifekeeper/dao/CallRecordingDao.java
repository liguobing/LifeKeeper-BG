package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.netdisk.record.ContactBean;
import com.lixyz.lifekeeper.bean.netdisk.record.RecordBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface CallRecordingDao {

    /**
     * 插入录音
     */
    @Insert("Insert into PhoneRecord (ObjectId,RecordId,Sha1,ContactId,CallTime,InOrOut,SourceFileName,OriginalFileName,RecordUser,RecordStatus,CreateTime) " +
            "values (#{record.objectId},#{record.recordId},#{record.sha1},#{record.contactId},#{record.callTime},#{record.inOrOut},#{record.sourceFileName},#{record.originalFileName},#{record.recordUser},#{record.recordStatus},#{record.createTime})")
    long insertRecord(
            @Param("record") RecordBean record
    ) throws SQLException;


    /**
     * 删除通话录音（RecordStatus = -1）
     */
    @Update({
            "<script>",
            "Update PhoneRecord set RecordStatus = -1",
            "where ObjectId in",
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteRecordById(@Param("list") List<String> list) throws SQLException;

    /**
     * 获取某个用户的录音总数
     */
    @Select("select count(ObjectId) from PhoneRecord where RecordUser = #{userId} and RecordStatus = 1")
    long queryRecordCountByUserId(@Param("userId") String userId) throws SQLException;

    @Select("select * from PhoneContact where ContactStatus = 1 and  ContactUser = #{userId}  group by ContactId order by OrderIndex ASC")
    ArrayList<ContactBean> queryContactName(@Param("userId") String userId);

    @Select("Select * from PhoneRecord where RecordUser = #{userId} and ContactId = #{contactId} and RecordStatus = 1 order by CallTime DESC")
    ArrayList<RecordBean> queryRecordByContactId(@Param("contactId") String contactId, @Param("userId") String userId);

    @Select("Select ContactId from PhoneContact where ContactStatus = 1 and ContactName = #{contactName} and ContactUser = #{recordUser} limit 1")
    String getContactIdByContactName(@Param("contactName") String contactName, @Param("recordUser") String recordUser);

    @Select("Select count(ObjectId) from PhoneRecord where Sha1 = #{sha1}")
    int fileIsExists(@Param("sha1") String sha1);

    @Insert("Insert into PhoneContact(ObjectId,ContactId,ContactName,PhoneNumber,ContactUser,ContactStatus) values (#{bean.objectId},#{bean.contactId},#{bean.contactName},#{bean.phoneNumber},#{bean.contactUser},#{bean.contactStatus})")
    void insertContact(@Param("bean") ContactBean contactBean);

    @Select("Select * from PhoneContact where ContactStatus = 1 and PhoneNumber = #{number} and ContactUser = #{userId}")
    String getContactIdByContactNumber(@Param("number") String phoneNumber, @Param("userId") String userId);

    @Select("Select * from PhoneRecord where RecordStatus = 1 and RecordUser = #{userId} and ContactId = #{contactId} order by callTime desc")
    ArrayList<RecordBean> getRecordByContactId(@Param("userId") String userId, @Param("contactId") String contactId);

    @Update({
            "<script>",
            "Update PhoneContact set ContactStatus = -1 ",
            "where ContactUser = #{userId} and  ContactId in",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int deleteContact(@Param("userId") String userId, @Param("ids") ArrayList<String> contactIds);
}
