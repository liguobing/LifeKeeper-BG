package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.record.*;
import com.lixyz.lifekeeper.dao.WeChatRecordDao;
import com.lixyz.lifekeeper.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class WeChatRecordService {
    private final WeChatRecordDao dao;

    public WeChatRecordService(WeChatRecordDao weChatRecordDao) {
        this.dao = weChatRecordDao;
    }

    public Result uploadWeChatRecord(MultipartFile file, String fileName, String userId) {
        try {
            if (file.isEmpty()) {
                return new Result(false, "文件为空", new Exception("文件为空"), null);
            }

            //微信录音 陈YiZhi_20230523144326.aac
            fileName = fileName.replace("微信录音 ", "");
            String contactName = fileName.split("_")[0];
            String callTime = fileName.split("\\.")[0].split("_")[1];

            //先看看目录存在不存在，不存在的话，创建之
            File userDir = new File("/files/LifeKeeperWeChatRecord/" + userId);
            if (!userDir.exists()) {
                boolean mkdir = userDir.mkdirs();
                if (!mkdir) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建通话录音目录出错"), null);
                }
            }
            WeChatRecordBean recordBean = new WeChatRecordBean();
            recordBean.setObjectId(UUID.randomUUID().toString());
            recordBean.setRecordId(UUID.randomUUID().toString());
            //查看联系人是否存在，不存在，则添加
            String contactId = dao.getWeChatContactIdByContactName(contactName, userId);
            if (contactId == null) {
                contactId = UUID.randomUUID().toString();
                WeChatRecordContactBean contactBean = new WeChatRecordContactBean();
                contactBean.setObjectId(UUID.randomUUID().toString());
                contactBean.setContactId(contactId);
                contactBean.setContactName(contactName);
                contactBean.setContactUser(userId);
                contactBean.setContactStatus(1);
                dao.insertWeChatContact(contactBean);
            } else {
                String id = dao.getContactIdByContactName(contactName, userId);
                if (id == null) {
                    WeChatRecordContactBean contactBean = new WeChatRecordContactBean();
                    contactBean.setObjectId(UUID.randomUUID().toString());
                    contactBean.setContactId(contactId);
                    contactBean.setContactName(contactName);
                    contactBean.setContactUser(userId);
                    contactBean.setContactStatus(1);
                    dao.insertWeChatContact(contactBean);
                }
            }
            recordBean.setContactId(contactId);
            File dest = new File("/files/LifeKeeperWeChatRecord/" + userId + "/" + fileName);
            file.transferTo(dest);
            //通过sha1查看文件是否存在
            String sha1 = FileUtil.getFileSha1(dest);
            int count = dao.fileIsExists(sha1);
            if (count > 0) {
                return new Result(true, "文件已经存在", null, null);
            }
            recordBean.setSha1(sha1);
            recordBean.setCallTime(callTime);
            recordBean.setSourceFileName(fileName);
            recordBean.setOriginalFileName(fileName);
            recordBean.setRecordUser(userId);
            recordBean.setRecordStatus(1);
            recordBean.setCreateTime(System.currentTimeMillis());
            dao.insertWeChatRecord(recordBean);
            return new Result(true, null, null, null);
        } catch (IOException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result deleteWeChatRecord(ArrayList<String> objectIds) {
        try {
            int deleteResult = dao.deleteWeChatRecordById(objectIds);
            return new Result(true, null, null, deleteResult > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result queryWeChatRecordCountByUserId(String userId) {
        try {
            return new Result(true, null, null, dao.queryWeChatRecordCountByUserId(userId));
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getWeChatRecordByName(String userId) {
        ArrayList<WeChatRecordContactBean> contacts = dao.queryWeChatContactName(userId);
        ArrayList<WeChatRecordRespBean> respLists = new ArrayList<>();
        for (WeChatRecordContactBean contact : contacts) {
            WeChatRecordRespBean bean = new WeChatRecordRespBean();
            ArrayList<WeChatRecordBean> records = dao.queryWeChatRecordByContactId(contact.getContactId(), userId);
            bean.setContact(contact);
            bean.setRecords(records);
            respLists.add(bean);
        }
        return new Result(true, null, null, respLists);
    }

    public Result getWeChatContactName(String userId) {
        ArrayList<WeChatRecordContactBean> contacts = dao.queryWeChatContactName(userId);
        return new Result(true, null, null, contacts);
    }

    public Result getWeChatRecordByContactId(String userId, String contactId) {
        ArrayList<WeChatRecordBean> list = dao.getWeChatRecordByContactId(userId, contactId);
        return new Result(true, null, null, list);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result deleteWeChatContact(String userId, ArrayList<String> contactIds) {
        int deleteCount = dao.deleteWeChatContact(userId, contactIds);
        return new Result(deleteCount == contactIds.size(), null, null, null);
    }
}
