package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Overview;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.service.*;
import com.lixyz.lifekeeper.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@Api(tags = "项目整体信息相关接口")
public class LifeKeeperController {

    private final BillService billService;
    private final PlanService planService;
    private final ImageService imageService;
    private final CallRecordingService callRecordingService;
    private final VideoService videoService;
    private final LifeKeeperService lifeKeeperService;

    public LifeKeeperController(LifeKeeperService lifeKeeperService, BillService billService, PlanService planService, ImageService imageService, CallRecordingService callRecordingService, VideoService videoService) {
        this.billService = billService;
        this.planService = planService;
        this.imageService = imageService;
        this.callRecordingService = callRecordingService;
        this.videoService = videoService;
        this.lifeKeeperService = lifeKeeperService;
    }


    @GetMapping("/GetOverviewByPhone")
    @ApiOperation("根据手机号获取软件数据概述")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "phone", dataType = "String", paramType = "query")
    }
    )
    public Result getOverviewByPhone(String phone) {
        long monthStart = TimeUtil.getCurrentMonthStart();
        long monthEnd = TimeUtil.getCurrentMonthEnd();
        long dayStart = TimeUtil.getTodayStart();
        long dayEnd = TimeUtil.getTodayEnd();
        try {
            UserBean userBean = lifeKeeperService.getUserByPhone(phone);
            String userId = userBean.getUserId();
            long imageCount = imageService.getIMageOverview(userId);
            long recordCount = callRecordingService.queryRecordOverview(userId);
            long videoCount = videoService.queryVideoOverview(userId);
            long lastUpdateTime = lifeKeeperService.getLastUpdateTime(userId);
            Overview overview = new Overview();
            overview.setUserBean(userBean);
            overview.setIncomeCount(billService.getCurrentMonthIncomeMoneyCount(userId, monthStart, monthEnd));
            overview.setExpendCount(billService.getCurrentMonthExpendMoneyCount(userId, monthStart, monthEnd));
            overview.setPlanCountOfDay(planService.getPlanCountOfDay(userId, dayStart, dayEnd));
            overview.setPlanCountOfMonth(planService.getPlanCountOfMonth(userId, monthStart, monthEnd));
            overview.setFileCount((int) (imageCount + recordCount + videoCount));
            return new Result(true, null, null, overview);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }
}