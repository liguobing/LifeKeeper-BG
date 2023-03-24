package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.plan.PlanRule;
import com.lixyz.lifekeeper.service.PlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Api(tags = "计划管理相关接口")
public class PlanController {
    private final PlanService service;

    public PlanController(PlanService service) {
        this.service = service;
    }

    /**
     * 添加计划
     */
    @PostMapping(value = "/AddPlan")
    @ApiOperation("添加计划的接口")
    public Result addPlan(HttpServletRequest request, @RequestBody PlanRule planRule) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.addPlan(planRule);
        }
    }


    @GetMapping("/GetPlanOverview")
    public Result getPlanOverview(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getPlanOverview(userId);
        }
    }

    @GetMapping("/GetPlans")
    public Result getPlans(HttpServletRequest request, @RequestParam int year, @RequestParam int month, @RequestParam int day) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getPlans(userId, year, month, day);
        }
    }

    @GetMapping("/GetMonthPlanList")
    public Result getMonthPlanList(HttpServletRequest request, @RequestParam int year, @RequestParam int month) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getMonthPlanList(userId, year, month);
        }
    }

    @GetMapping("/GetPlansForMonth")
    public Result getPlansForMonth(HttpServletRequest request, @RequestParam int year, @RequestParam int month) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getPlans(userId, year, month);
        }
    }

    @GetMapping("/ResetAlarm")
    public Result resetAlarm(HttpServletRequest request, @RequestParam String objectId, @RequestParam int alarmTime) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.resetAlarm(objectId, alarmTime);
        }
    }

    @GetMapping("/FinishPlan")
    public Result finishPlan(HttpServletRequest request, @RequestParam String objectId) {
        System.out.println("FinishPlan" + objectId);
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.finishPlan(objectId);
        }
    }

    @GetMapping("/BackoutFinishedPlan")
    public Result backoutFinishedPlan(HttpServletRequest request, @RequestParam String objectId) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.backoutFinishedPlan(objectId);
        }
    }

    @GetMapping("/DeleteGroupPlans")
    public Result deleteGroupPlans(HttpServletRequest request, @RequestParam String objectId, @RequestParam String groupId) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.deleteGroupPlans(groupId);
        }
    }
    @GetMapping("/DeleteSinglePlan")
    public Result deleteSinglePlan(HttpServletRequest request, @RequestParam String objectId) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.deleteSinglePlan(objectId);
        }
    }

    @GetMapping("/GetAllNotification")
    public Result getAllNotification(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getAllNotification(userId);
        }
    }
}
