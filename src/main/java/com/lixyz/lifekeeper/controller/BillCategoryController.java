package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.service.BillCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@Api(tags = "账单分类相关接口")
public class BillCategoryController {

    private final BillCategoryService billCategoryService;

    public BillCategoryController(BillCategoryService billCategoryService) {
        this.billCategoryService = billCategoryService;
    }

    @PostMapping(value = "/AddBillCategory")
    @ApiOperation("添加账单分类的接口")
    public Result addBillCategory(HttpServletRequest request, @RequestBody BillCategory billCategory) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billCategoryService.insertBillCategory(billCategory);
        }
    }

    @PostMapping("/DeleteBillCategory")
    @ApiOperation("删除账单分类的接口")
    public Result deleteBillCategory(HttpServletRequest request, @RequestBody ArrayList<String> objectIdList) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billCategoryService.deleteBillCategory(objectIdList);
        }
    }

    @GetMapping(value = "GetBillCategoryData")
    public Result getBillCategory(HttpServletRequest request){
        String userId = request.getHeader("Token");
        if(userId == null){
            return new Result(false,"未登录操作",null,null);
        }else{
            return billCategoryService.getBillCategory(userId);
        }
    }

    @PostMapping(value = "/UpdateBillCategory")
    @ApiOperation("更新账单分类的接口")
    public Result updateBillCategory(HttpServletRequest request, @RequestBody BillCategory category) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billCategoryService.updateBillCategory(category);
        }
    }

    @PostMapping(value = "/UpdateBillCategoryOrder")
    @ApiOperation("更新账单分类排序的接口")
    public Result updateBillCategoryOrder(@RequestBody List<BillCategory> newOrderBills) {
        return billCategoryService.updateBillCategoryOrder(newOrderBills);
    }
}
