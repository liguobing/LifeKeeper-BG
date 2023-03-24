package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.dao.LifeKeeperDao;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class LifeKeeperService {
    private final LifeKeeperDao dao;

    public LifeKeeperService(LifeKeeperDao dao) {
        this.dao = dao;
    }

    public long getLastUpdateTime(String userId) throws SQLException {
        return dao.getLastUpdateTime(userId);
    }

    public String getUserName(String userId) throws SQLException {
        return dao.getUserName(userId);
    }

    public String getUserIconUrl(String userId) throws SQLException {
        return dao.getUserIconUrl(userId);
    }

    public UserBean getUserByPhone(String phone) {
        return dao.getUserByPhone(phone);
    }

    public UserBean getUserByUserId(String userId) {
        return dao.getUserByUserId(userId);
    }
}
