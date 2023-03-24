package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.service.CallRecordingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
@CrossOrigin
@Api(tags = "通话录音管理相关接口")
public class CallRecordingController {
    private final CallRecordingService service;

    public CallRecordingController(CallRecordingService callRecordingService) {
        this.service = callRecordingService;
    }

    /**
     * 上传录音文件
     *
     * @param file 文件
     * @return 文件上传是否成功
     */
    @PostMapping(value = "/UploadRecord",produces = "application/json;charset=UTF-8")
    @ApiOperation("上传录音文件")
    public Result uploadRecord(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.uploadRecord(file, fileName,userId);
        }
    }

    @PostMapping("/DeleteRecord")
    @ApiOperation("删除录音文件")
    public Result deleteRecord(@RequestBody ArrayList<String> objectIds) {
        return service.deleteRecord(objectIds);
    }

    @GetMapping("/GetRecordCountByUserId")
    @ApiOperation("获取用户的录音总数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户 UserId", dataType = "String", paramType = "query")
    }
    )
    public Result getRecordCountByUserId(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.queryRecordCountByUserId(userId);
        }
    }

    @GetMapping("/GetRecordGroupContact")
    public Result getRecordGroupContact(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getRecordByName(userId);
        }
    }

    @GetMapping("/GetContactName")
    public Result getContactName(HttpServletRequest request) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return service.getContactName(userId);
        }
    }

    @GetMapping("/GetRecordByContactId")
    public Result getRecordByContactId(HttpServletRequest request){
        String userId = request.getHeader("Token");
        String contactId = request.getHeader("ContactId");
        if(userId == null){
            return new Result(false, "未登录操作", null, null);
        }else{
            return service.getRecordByContactId(userId,contactId);
        }
    }

    @PostMapping("/DeleteContact")
    public Result deleteContact(HttpServletRequest request,@RequestBody ArrayList<String> contactIds){
        String userId = request.getHeader("Token");
        if(userId == null){
            return new Result(false, "未登录操作", null, null);
        }else{
            return service.deleteContact(userId,contactIds);
        }
    }
}
