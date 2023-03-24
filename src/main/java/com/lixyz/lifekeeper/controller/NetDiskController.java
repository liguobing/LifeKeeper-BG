package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.service.NetDiskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "网盘相关接口")
public class NetDiskController {
    private final NetDiskService service;

    public NetDiskController(NetDiskService service) {
        this.service = service;
    }

    @GetMapping("/GetNetDiskData")
    @ApiOperation("获取网盘数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户 UserId", dataType = "String", paramType = "query")
    }
    )
    public Result getNetDiskData(String userId) {
        return service.getNetDiskData(userId);
    }

    @GetMapping("/GetNetDiskOverview")
    public Result getNetDiskOverview(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getNetDiskData(userId);
        }
    }
}
