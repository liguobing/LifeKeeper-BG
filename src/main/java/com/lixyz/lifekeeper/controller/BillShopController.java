package com.lixyz.lifekeeper.controller;

import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billshop.BillShop;
import com.lixyz.lifekeeper.service.BillShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@CrossOrigin
@Api(tags = "账单商家管理相关接口")
public class BillShopController {

    private final BillShopService billShopService;

    public BillShopController(BillShopService billShopService) {
        this.billShopService = billShopService;
    }

    @PostMapping(value = "/AddBillShop")
    @ApiOperation("添加账单商家的接口")
    public Result addBillShop(HttpServletRequest request,
                              @RequestBody BillShop billShop) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billShopService.insertBillShop(new Gson().fromJson(new Gson().toJson(billShop), BillShop.class));
        }
    }

    @PostMapping(value = "/DeleteShop", consumes = "application/json")
    @ApiOperation("添加账单商家的接口")
    public Result deleteShop(HttpServletRequest request,
                              @RequestBody ArrayList<String> billShop) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billShopService.deleteBillShop(billShop);
        }
    }




    @GetMapping("/GetOftenUseBillShop")
    @ApiOperation("获取用户常用商家")
    public Result getOftenUseBillShop(HttpServletRequest request, @RequestParam("offset") int offset, @RequestParam("rows") int rows) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billShopService.getOftenUseBillShop(userId, offset, rows);
        }
    }

    @GetMapping("/GetAllCustomShops")
    @ApiOperation("获取所有自定义商家")
    public Result getAllCustomShops(HttpServletRequest request, int offset, int rows) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billShopService.getAllCustomShops(userId, offset, rows);
        }
    }

    @PostMapping("UpdateShop")
    @ApiOperation("更新账单商家")
    public Result updateShop(HttpServletRequest request,
                             @RequestBody BillShop billShop) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billShopService.updateShop(billShop);
        }
    }

    @GetMapping("GetBillShopData")
    @ApiOperation("更新账单商家")
    public Result getBillShopData(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billShopService.getBillShop(userId);
        }
    }
}
