package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.service.CallRecordingService;
import com.lixyz.lifekeeper.service.WeChatRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@CrossOrigin
@Api(tags = "微信录音管理相关接口")
public class WeChatRecordController {
    private final WeChatRecordService service;

    public WeChatRecordController(WeChatRecordService weChatRecordService) {
        this.service = weChatRecordService;
    }

    /**
     * 上传微信录音文件
     *
     * @param file 文件
     * @return 文件上传是否成功
     */
    @PostMapping(value = "/UploadWeChatRecord",produces = "application/json;charset=UTF-8")
    @ApiOperation("上传微信录音文件")
    public Result uploadWeChatRecord(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.uploadWeChatRecord(file, fileName,userId);
        }
    }

    @PostMapping("/DeleteWeChatRecord")
    @ApiOperation("删除微信录音文件")
    public Result deleteWeChatRecord(@RequestBody ArrayList<String> objectIds) {
        return service.deleteWeChatRecord(objectIds);
    }

    @GetMapping("/GetWeChatRecordCountByUserId")
    @ApiOperation("获取用户的微信录音总数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户 UserId", dataType = "String", paramType = "query")
    }
    )
    public Result getWeChatRecordCountByUserId(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.queryWeChatRecordCountByUserId(userId);
        }
    }

    @GetMapping("/GetWeChatRecordGroupContact")
    public Result getWeChatRecordGroupContact(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getWeChatRecordByName(userId);
        }
    }

    @GetMapping("/GetWeChatContactName")
    public Result getWeChatContactName(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getWeChatContactName(userId);
        }
    }


    @GetMapping("/GetWeChatRecordByContactId")
    public Result getWeChatRecordByContactId(HttpServletRequest request){
        String userId = request.getHeader("Token");
        String contactId = request.getHeader("ContactId");
        if(userId == null){
            return new Result(false, "未登录操作", null, null);
        }else{
            return service.getWeChatRecordByContactId(userId,contactId);
        }
    }

    @PostMapping("/DeleteWeChatContact")
    public Result deleteWeChatContact(HttpServletRequest request,@RequestBody ArrayList<String> contactIds){
        String userId = request.getHeader("Token");
        if(userId == null){
            return new Result(false, "未登录操作", null, null);
        }else{
            return service.deleteWeChatContact(userId,contactIds);
        }
    }

}
