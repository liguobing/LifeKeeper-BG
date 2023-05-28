package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.netdisk.NetDiskOverview;
import com.lixyz.lifekeeper.dao.NetDiskDao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class NetDiskService {
    private final NetDiskDao dao;

    public NetDiskService(NetDiskDao dao) {
        this.dao = dao;
    }

    public Result getNetDiskData(String userId) {
        try {
            int imageCount = dao.getImageCount(userId);
            int recordCount = dao.getRecordCount(userId);
            int videoCount = dao.getVideoCount(userId);
            int weChatRecordCount = dao.getWeChatRecordCount(userId);
            NetDiskOverview overview = new NetDiskOverview();
            overview.setImageCount(imageCount);
            overview.setRecordCount(recordCount);
            overview.setVideoCount(videoCount);
            overview.setWeChatRecordCount(weChatRecordCount);
            return new Result(true, null, null, overview);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }
}
