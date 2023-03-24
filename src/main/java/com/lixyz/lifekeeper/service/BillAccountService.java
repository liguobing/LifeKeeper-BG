package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccountOverview;
import com.lixyz.lifekeeper.dao.BillAccountDao;
import com.lixyz.lifekeeper.util.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillAccountService {

    private final BillAccountDao billAccountDao;

    public BillAccountService(BillAccountDao billAccountDao) {
        this.billAccountDao = billAccountDao;
    }

    /**
     * 添加账单账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Result insertBillAccount(BillAccount billAccount) {
        try {
            long count = billAccountDao.accountIsExists(billAccount.getAccountUser(), billAccount.getAccountName());
            if (count > 0) {
                return new Result(false, "账户已经存在，无需重复添加", null, false);
            } else {
                long topIndex = billAccountDao.getTopIndex(billAccount.getAccountUser());
                billAccount.setOrderIndex((int) topIndex + 1);
                int addResult = billAccountDao.insertBillAccount(billAccount);
                return new Result(true, null, null, addResult > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 删除账单账户
     */
    public Result deleteBillAccount(ArrayList<String> objectIdList) {
        try {
            int deleteResult = billAccountDao.deleteBillAccount(objectIdList);
            return new Result(true, null, null, deleteResult > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 根据用户 ID 搜索账单账户
     */
    public Result selectBillAccountByUserId(String userId) {
        try {
            List<BillAccount> list = billAccountDao.selectBillAccountByUserId(userId);
            return new Result(true, null, null, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    /**
     * 更新账单账户
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateBillAccount(BillAccount account) {
        try {
            if (billAccountDao.accountIsExists(account.getAccountUser(), account.getAccountName()) > 0) {
                return new Result(false, "该名称已经存在", null, null);
            } else {
                int updateResult = billAccountDao.updateBillAccount(account, System.currentTimeMillis());
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
    public Result updateBillAccountOrder(List<BillAccount> newOrderBillAccounts) {
        boolean result = true;
        try {
            for (BillAccount bean : newOrderBillAccounts) {
                int updateResult = billAccountDao.updateBillAccountOrder(bean.getObjectId(), bean.getOrderIndex());
                result = result & updateResult > 0;
            }
            return new Result(true, null, null, result);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result getAccountOverview(String userId) {
        try {
            long monthStart = TimeUtil.getCurrentMonthStart();
            long monthEnd = TimeUtil.getCurrentMonthEnd();
            Double incomeCount = billAccountDao.getCurrentMonthIncomeCount(userId, monthStart, monthEnd);
            Double expendCount = billAccountDao.getCurrentMonthExpendCount(userId, monthStart, monthEnd);
            return new Result(true, null, null, new BillAccountOverview(incomeCount, expendCount));
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getBillAccount(String userId) {
        try {
            ArrayList<BillAccount> list = billAccountDao.getBillAccount(userId);
            return new Result(true, null, null, list);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }
}
