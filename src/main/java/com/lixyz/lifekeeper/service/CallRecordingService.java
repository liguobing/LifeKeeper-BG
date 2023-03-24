package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.record.ContactBean;
import com.lixyz.lifekeeper.bean.netdisk.record.RecordBean;
import com.lixyz.lifekeeper.bean.netdisk.record.RecordRespBean;
import com.lixyz.lifekeeper.dao.CallRecordingDao;
import com.lixyz.lifekeeper.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CallRecordingService {
    private final CallRecordingDao dao;

    public CallRecordingService(CallRecordingDao callRecordingDao) {
        this.dao = callRecordingDao;
    }

    /**
     * 上传录音
     */
    public Result uploadRecord(MultipartFile file, String fileName, String userId) {
        try {
            if (file.isEmpty()) {
                return new Result(false, "文件为空", new Exception("文件为空"), null);
            }
            String contactName = fileName.split("\\(")[0];
            String phoneNumber = fileName.split("\\(")[1].split("\\)")[0];
            String callTime = fileName.split("\\.")[0].split("_")[1];

            //先看看目录存在不存在，不存在的话，创建之
            File userDir = new File("/files/LifeKeeperCallRecord/" + userId);
            if (!userDir.exists()) {
                boolean mkdir = userDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建通话录音目录出错"), null);
                }
            }
            RecordBean recordBean = new RecordBean();
            recordBean.setObjectId(UUID.randomUUID().toString());
            recordBean.setRecordId(UUID.randomUUID().toString());
            //查看联系人是否存在，不存在，则添加
            String contactId = dao.getContactIdByContactName(contactName, userId);
            if (contactId == null) {
                contactId = UUID.randomUUID().toString();
                ContactBean contactBean = new ContactBean();
                contactBean.setObjectId(UUID.randomUUID().toString());
                contactBean.setContactId(contactId);
                contactBean.setContactName(contactName);
                contactBean.setPhoneNumber(phoneNumber);
                contactBean.setContactUser(userId);
                contactBean.setContactStatus(1);
                dao.insertContact(contactBean);
            } else {
                String id = dao.getContactIdByContactNumber(phoneNumber, userId);
                if (id == null) {
                    ContactBean contactBean = new ContactBean();
                    contactBean.setObjectId(UUID.randomUUID().toString());
                    contactBean.setContactId(contactId);
                    contactBean.setContactName(contactName);
                    contactBean.setPhoneNumber(phoneNumber);
                    contactBean.setContactUser(userId);
                    contactBean.setContactStatus(1);
                    dao.insertContact(contactBean);
                }
            }
            recordBean.setContactId(contactId);
            File dest = new File("/files/LifeKeeperCallRecord/" + userId + "/" + fileName);
            file.transferTo(dest);
            //通过sha1查看文件是否存在
            String sha1 = FileUtil.getFileSha1(dest);
            int count = dao.fileIsExists(sha1);
            if (count > 0) {
                return new Result(true, "文件已经存在", null, null);
            }
            recordBean.setSha1(sha1);
            recordBean.setCallTime(callTime);
            recordBean.setInOrOut(1);
            recordBean.setSourceFileName(fileName);
            recordBean.setOriginalFileName(fileName);
            recordBean.setRecordUser(userId);
            recordBean.setRecordStatus(1);
            recordBean.setCreateTime(System.currentTimeMillis());

            dao.insertRecord(recordBean);
            return new Result(true, null, null, null);
        } catch (IOException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 删除录音
     */
    @Transactional(rollbackFor = Exception.class)
    public Result deleteRecord(List<String> objectIds) {
        try {
            int deleteResult = dao.deleteRecordById(objectIds);
            return new Result(true, null, null, deleteResult > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 获取录音总数
     */
    public Result queryRecordCountByUserId(String userId) {
        try {
            return new Result(true, null, null, dao.queryRecordCountByUserId(userId));
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    /**
     * 获取录音概述
     */
    public long queryRecordOverview(String userId) throws SQLException {
        return dao.queryRecordCountByUserId(userId);
    }

    public Result getRecordByName(String userId) {
        ArrayList<ContactBean> contacts = dao.queryContactName(userId);
        ArrayList<RecordRespBean> respLists = new ArrayList<>();
        for (ContactBean contact : contacts) {
            RecordRespBean bean = new RecordRespBean();
            ArrayList<RecordBean> records = dao.queryRecordByContactId(contact.getContactId(), userId);
            bean.setContact(contact);
            bean.setRecords(records);
            respLists.add(bean);
        }
        return new Result(true, null, null, respLists);
    }

    public Result getContactName(String userId) {
        ArrayList<ContactBean> contacts = dao.queryContactName(userId);
        return new Result(true, null, null, contacts);
    }

    public Result getRecordByContactId(String userId, String contactId) {
        ArrayList<RecordBean> list = dao.getRecordByContactId(userId, contactId);
        return new Result(true, null, null, list);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result deleteContact(String userId, ArrayList<String> contactIds) {
        int deleteCount = dao.deleteContact(userId, contactIds);
        return new Result(deleteCount == contactIds.size(), null, null, null);
    }
}