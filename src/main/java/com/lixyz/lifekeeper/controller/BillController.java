package com.lixyz.lifekeeper.controller;

import cn.hutool.core.date.DateUtil;
import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.bill.Bill;
import com.lixyz.lifekeeper.bean.bill.bill.BillImageBean;
import com.lixyz.lifekeeper.service.BillService;
import com.lixyz.lifekeeper.socket.SocketMessage;
import com.lixyz.lifekeeper.socket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@RestController
@CrossOrigin
@Api(tags = "账单管理相关接口")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping(value = "/AddBill", produces = "application/json;charset=UTF-8")
    @ApiOperation("添加账单的接口")
    public Result addBill(HttpServletRequest request, @RequestBody Bill bill) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billService.insertBill(bill);
        }
    }

    @GetMapping("/DeleteBill")
    @ApiOperation("删除账单的接口")
    public Result deleteBill(HttpServletRequest request, @RequestParam("objectId") String objectId) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billService.deleteBill(objectId);
        }
    }

    @GetMapping("/GetWebTableData")
    @ApiOperation("获取 WEB 端表格信息")
    public Result getWebTableData(HttpServletRequest request,
                                  @RequestParam("start") long start,
                                  @RequestParam("end") long end,
                                  @RequestParam("pageNum") int pageNum,
                                  @RequestParam("pageSize") int pageSize,
                                  @RequestParam("sortName") String sortName,
                                  @RequestParam("sortOrder") String sortOrder) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billService.getRangeBills(userId, start, end, pageNum, pageSize, sortName, sortOrder);
        }
    }

    @GetMapping("/GetBillByMonth")
    @ApiOperation("获取某年某月的账单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户的 UserId", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "Integer", paramType = "query")
    }
    )
    public Result getBillByMonth(HttpServletRequest request, @RequestParam int year, @RequestParam int month) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billService.getBillByMonth(userId, year, month);
        }
    }

    @GetMapping(value = "/GetBillsByTimeRange")
    @ApiOperation("获取时间段内账单(单独针对微信小程序)")
    public Result getBillsByTimeRange(HttpServletRequest request) {
        String userId = request.getHeader("UserId");
        String year = request.getHeader("Year");
        String month = request.getHeader("Month");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billService.getBillsByTimeRange(userId, Integer.parseInt(year), Integer.parseInt(month));
        }
    }

    /**
     * 上传文件
     *
     * @param files 文件
     * @return 文件上传响应对象
     */
    @PostMapping("/UploadBillImage")
    @ApiOperation("上传文件")
    public Result uploadImg(HttpServletRequest request,
                            @RequestParam("sourceFile") MultipartFile[] files,
                            @RequestParam("billId") String billId) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            for (MultipartFile file : files) {
                System.out.println(file.getOriginalFilename());
            }
            System.out.println(billId);
            return billService.uploadImg(files, userId, billId);
        }
    }


    @GetMapping(value = "/GetBillChartData", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取某个时间段账单图表数据")
    public Result getBillChartData(HttpServletRequest request, @RequestParam("start") long start, @RequestParam("end") long end) {
        String token = request.getHeader("Token");
        if (null == token) {
            return new Result(false, "您还没有登录，请返回主页登录。", null, null);
        } else {
            return billService.getBillChartData(token, start, end);
        }
    }

    @GetMapping(value = "/GetRangeOverview", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取某个时间段的账单概况")
    public Result getRangeOverview(HttpServletRequest request,
                                   @RequestParam("start") long start,
                                   @RequestParam("end") long end
    ) {
        String token = request.getHeader("Token");
        if (token == null) {
            return new Result(false, "您还没有登录，请返回主页登录。", null, null);
        } else {
            return billService.getRangeOverview(token, start, end);
        }
    }


    @GetMapping(value = "/GetBillCategoryAndBillAccount", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取某个时间段的账单")
    public Result getBillCategoryAndBillAccount(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return billService.getBillCategoryAndBillAccount(userId);
        }
    }

    @GetMapping(value = "/GetBillByCategory", produces = "application/json;charset=UTF-8")
    @ApiOperation("获取某个时间段的某个分类下的账单")
    public Result getBillByCategory(HttpServletRequest request,
                                    @RequestParam String categoryName,
                                    @RequestParam int billProperty,
                                    @RequestParam int year,
                                    @RequestParam int month) {
        String userId = request.getHeader("Token");
        if (null == userId) {
            return new Result(false, "未登录操作", null, null);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            return billService.getBillByCategory(userId, categoryName, billProperty, DateUtil.beginOfMonth(calendar).getTimeInMillis(), DateUtil.endOfMonth(calendar).getTimeInMillis());
        }
    }

    @PostMapping("/ImportWechatBill")
    @ApiOperation("导入微信账单")
    public Result importWeChatBill(HttpServletRequest request,
                                   @RequestParam("sourceFile") MultipartFile sourceFile) {
        try {
            String userId = request.getHeader("Token");
            if (userId == null) {
                return new Result(false, "未登录操作", null, null);
            } else {
                //文件是否为空
                if (sourceFile.isEmpty()) {
                    return new Result(false, "文件为空", null, null);
                }
                File dir = new File("/files/LifeKeeperImportBillWeChat/" + userId);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dest = new File("/files/LifeKeeperImportBillWeChat/" + userId + "/" + userId + "-" +  System.currentTimeMillis() + "-" + UUID.randomUUID() + ".csv");
                sourceFile.transferTo(dest);
                return new Result(true, null, null, dest.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage(), null, null);
        }
    }
}
