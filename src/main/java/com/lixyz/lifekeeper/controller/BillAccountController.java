package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.service.BillAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@Api(tags = "账单账户相关接口")
public class BillAccountController {

    private final BillAccountService billAccountService;

    public BillAccountController(BillAccountService billAccountService) {
        this.billAccountService = billAccountService;
    }

    @PostMapping(value = "/AddBillAccount")
    @ApiOperation("添加账单账户的接口")
    public Result addBillAccount(HttpServletRequest request, @RequestBody BillAccount billAccount) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billAccountService.insertBillAccount(billAccount);
        }
    }

    @PostMapping("/DeleteBillAccount")
    @ApiOperation("删除账单账户的接口")
    public Result deleteBillAccount(HttpServletRequest request, @RequestBody ArrayList<String> objectIdList) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billAccountService.deleteBillAccount(objectIdList);
        }
    }


    @GetMapping("/GetAccountOverview")
    @ApiOperation("根据 UserId 获取账单账户")
    public Result getAccountOverview(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billAccountService.getAccountOverview(userId);
        }
    }

    @GetMapping("/GetBillAccountData")
    @ApiOperation("根据 UserId 获取账单账户")
    public Result getBillAccountData(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billAccountService.getBillAccount(userId);
        }
    }


    @PostMapping(value = "/UpdateBillAccount")
    @ApiOperation("更新账单账户")
    public Result updateBillAccount(HttpServletRequest request, @RequestBody BillAccount account) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billAccountService.updateBillAccount(account);
        }
    }

    @PostMapping(value = "/UpdateBillAccountOrder")
    @ApiOperation("更新账单账户排序")
    public Result updateBillAccountOrder(@RequestBody List<BillAccount> newOrderBills) {
        return billAccountService.updateBillAccountOrder(newOrderBills);
    }
}
