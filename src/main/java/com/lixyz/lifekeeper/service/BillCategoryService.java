package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.dao.BillCategoryDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillCategoryService {

    private final BillCategoryDao billCategoryDao;

    public BillCategoryService(BillCategoryDao billCategoryDao) {
        this.billCategoryDao = billCategoryDao;
    }

    /**
     * 新增账单分类
     */
    public Result insertBillCategory(BillCategory billCategory) {
        try {
            long count = billCategoryDao.categoryIsExists(billCategory.getCategoryUser(), billCategory.getCategoryName(), billCategory.getIsIncome());
            if (count > 0) {
                return new Result(false, "该分类已经存在，无需重复添加", null, false);
            } else {
                long topIndex = billCategoryDao.getTopIndex(billCategory.getCategoryUser());
                billCategory.setOrderIndex((int) topIndex + 1);
                int addResult = billCategoryDao.insertBillCategory(billCategory);
                return new Result(true, null, null, addResult > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 删除账单分类
     */
    public Result deleteBillCategory(ArrayList<String> objectIdList) {
        try {
            int deleteResult = billCategoryDao.deleteBillCategory(objectIdList);
            return new Result(true, null, null, deleteResult > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 根据用户 ID 查找该用户的所有账单分类
     */
    public Result selectBillCategoryByUserId(String userId, int isIncome) {
        try {
            List<BillCategory> list = billCategoryDao.selectBillCategoryByUserId(userId, isIncome);
            return new Result(true, null, null, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 更新账单分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateBillCategory(BillCategory category) {
        try {
            long l = billCategoryDao.categoryIsExists(category.getCategoryUser(), category.getCategoryName(), category.getIsIncome());
            if (l > 0) {
                return new Result(false, "该账单分类已经存在，无需重复添加", null, null);
            } else {
                int updateResult = billCategoryDao.updateBillCategory(category, System.currentTimeMillis());
                return new Result(updateResult > 0, null, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 更新账单分类的排序
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateBillCategoryOrder(List<BillCategory> newOrderBillCategories) {
        boolean result = true;
        try {
            for (BillCategory bean : newOrderBillCategories) {
                int updateResult = billCategoryDao.updateBillCategoryOrderIndex(bean.getObjectId(), bean.getOrderIndex());
                result = result & updateResult > 0;
            }
            return new Result(true, null, null, result);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result getBillCategory(String userId) {
        try {
            ArrayList<BillCategory> billCategory = billCategoryDao.getBillCategory(userId);
            return new Result(true, null, null, billCategory);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "数据库出错，请稍候重试...", null, null);
        }
    }
}
