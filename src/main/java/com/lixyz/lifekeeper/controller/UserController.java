package com.lixyz.lifekeeper.controller;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.user.UpdateUserBean;
import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.service.UserService;
import com.lixyz.lifekeeper.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@CrossOrigin
@RestController
@Api(tags = "用户管理相关接口")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/AddUser")
    @ApiOperation("添加用户")
    public Result addUser(@RequestBody UserBean user) {
        return userService.addUser(user);
    }


    @GetMapping("/Login")
    @ApiOperation("根据手机号和密码查询用户")
    public Result login(@RequestParam String phone, @RequestParam String password) {
        return userService.selectUserByUserPhoneAndPassword(phone, password);
    }

    @GetMapping("/VerifyCodeLogin")
    @ApiOperation("手机验证码登录，如果手机注册过，返回用户信息，如果没有注册过，直接注册为新用户")
    public Result verifyCodeLogin(@RequestParam String phone, @RequestParam String code, @RequestParam String stamp) {
        try {
            return userService.verifyCodeLogin(phone, code, stamp);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    @GetMapping("/PhoneIsRegistered")
    @ApiOperation("检查手机是否已经注册过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", paramType = "query")
    }
    )
    public Result phoneIsRegistered(String phone) {
        return userService.phoneIsRegistered(phone);
    }

    @GetMapping("/GetUserByWeiboId")
    @ApiOperation("根据 weiboId 查找用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userBindWeiboId", value = "用户 WeiboId", dataType = "String", paramType = "query")
    }
    )
    public Result getUserByWeiboId(String userBindWeiboId) {
        return userService.selectUserByWeiboId(userBindWeiboId);
    }

    @GetMapping("/GetUserByQQId")
    @ApiOperation("根据 QQId 查找用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userBindQQId", value = "用户 QQId", dataType = "String", paramType = "query")
    }
    )
    public Result selectUserByQQId(String userBindQQId) {
        return userService.selectUserByQQId(userBindQQId);
    }


    @PostMapping(value = "/UpdateUser")
    @ApiOperation("更新用户")
    public Result updateUser(@RequestBody UpdateUserBean updateUserBean) {
        return userService.updateUser(updateUserBean.getOldUserUpdateTime(), updateUserBean.getOldUserObjectId(), updateUserBean.getNewUser());
    }

    @PostMapping("/UploadUserIcon")
    @ApiOperation("上传用户头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "头像图片文件")
    }
    )
    public Result uploadImg(HttpServletRequest request, MultipartFile file) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return userService.uploadImg(userId, file);
        }
    }


    @GetMapping("/ResetPassword")
    @ApiOperation("修改密码")
    public Result resetPassword(HttpServletRequest request, String phone, String code, String codeStamp, String newPassword) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            try {
                return userService.resetPassword(userId, phone, code, codeStamp, newPassword);
            } catch (SQLException e) {
                e.printStackTrace();
                return new Result(false, null, null, null);
            }
        }
    }

    @GetMapping("/FindPassword")
    @ApiOperation("修改密码")
    public Result findPassword(String phone, String newPassword) {
        try {
            return userService.resetPassword(phone, newPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    @GetMapping("/ResetPhone")
    @ApiOperation("修改手机号")
    public Result resetPhone(HttpServletRequest request, String phone, String code, String codeStamp) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            try {
                return userService.resetPhone(userId, phone, code, codeStamp);
            } catch (SQLException e) {
                e.printStackTrace();
                return new Result(false, null, null, null);
            }
        }
    }

    @GetMapping("/ResetClientId")
    @ApiOperation("设置推送客户端 ID")
    public Result resetClientId(HttpServletRequest request, String clientId) {
        String userId = request.getHeader("Token");
        if (userId == null) {
            return new Result(false, "未登录操作", null, null);
        } else {
            return userService.resetClientId(userId, clientId);
        }
    }
}