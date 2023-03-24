package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billshop.BillShop;
import com.lixyz.lifekeeper.bean.bill.billshop.ShopResult;
import com.lixyz.lifekeeper.dao.BillShopDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillShopService {
    private final BillShopDao billShopDao;

    public BillShopService(BillShopDao billShopDao) {
        this.billShopDao = billShopDao;
    }

    /**
     * 插入商家
     */
    public Result insertBillShop(BillShop billShop) {
        try {
            int count = billShopDao.billShopIsExists(billShop.getShopUser(), billShop.getShopName());
            if (count > 0) {
                return new Result(false, "该商家已经存在", null, false);
            } else {
                int addResult = billShopDao.insertBillShop(billShop);
                return new Result(true, null, null, addResult > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 删除商家
     */
    @Transactional(rollbackFor = Exception.class)
    public Result deleteBillShop(ArrayList<String> objectIdList) {
        try {
            int deleteResult = billShopDao.deleteBillShop(objectIdList);
            if (deleteResult == objectIdList.size()) {
                return new Result(true, null, null, null);
            } else {
                return new Result(false, "删除商家出错", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 根据用户 ID 获取账单商家
     */
    public Result selectBillShopByUserId(String userId) {
        try {
            List<BillShop> billShops = billShopDao.selectBillShopByUserId(userId);
            return new Result(true, null, null, billShops);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 获取经常使用的商家
     */
    public Result getOftenUseBillShop(String userId, int offset, int rows) {
        try {
            int shopCount = billShopDao.getBillShopCount(userId);
            ArrayList<String> billShops = billShopDao.getOftenUseBillShop(userId, offset * rows, rows);
            ShopResult shopResult = new ShopResult();
            shopResult.setShopCount(shopCount);
            shopResult.setOffset(offset);
            shopResult.setShopNames(billShops);
            return new Result(true, null, null, shopResult);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 获取自定义商家
     */
    public Result getAllCustomShops(String userId, int offset, int rows) {
        try {
            int shopCount = billShopDao.getCustomShopCount(userId);
            ArrayList<String> billShops = billShopDao.getCustomShop(userId, offset * rows, rows);
            ShopResult shopResult = new ShopResult();
            shopResult.setShopCount(shopCount);
            shopResult.setOffset(offset);
            shopResult.setShopNames(billShops);
            return new Result(true, null, null, shopResult);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result updateShop(BillShop shop) {
        try {
            BillShop billShop = billShopDao.shopNameIsExists(shop.getShopName(), shop.getShopUser());
            if (billShop == null) {
                int i = billShopDao.updateShop(shop);
                return new Result(i > 0, null, null, null);
            } else {
                return new Result(false, "该商家名称已存在，无须重复添加", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getBillShop(String userId) {
        ArrayList<BillShop> billShops = billShopDao.getBillShop(userId);
        return new Result(true,null,null,billShops);
    }
}
