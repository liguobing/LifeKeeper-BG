package com.lixyz.lifekeeper.controller;

import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.recaptcha.RecaptchaBean;
import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.service.UserService;
import com.lixyz.lifekeeper.util.Constant;
import com.lixyz.lifekeeper.util.StringUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@Api(tags = "短信管理相关接口")
public class SMSController {

    private final UserService service;

    public SMSController(UserService userService) {
        this.service = userService;
    }


    @GetMapping("/RequestSMSCodeWithLogin")
    @ApiOperation("登录状态下请求短信验证码，会验证登录账号和申请手机账号是否相符")
    public Result requestSMSCodeWithLogin(HttpServletRequest request, String phone, String googleToken) {
        try {
            //先验证 recaptcha
            Request verifyRequest = new Request.Builder()
                    .url("https://www.recaptcha.net/recaptcha/api/siteverify?secret=" + Constant.RECAPTCHA_SECRET + "&response=" + googleToken)
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .writeTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .build();
            Response response = client.newCall(verifyRequest).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String json = responseBody.string();
                RecaptchaBean recaptchaBean = new Gson().fromJson(json, RecaptchaBean.class);
                //谷歌验证码验证成功
                if (recaptchaBean.getSuccess()) {
                    //验证手机号和 UserId 是否匹配
                    String userId = request.getHeader("Token");
                    if (userId == null) {
                        return new Result(false, "未登录操作", null, null);
                    } else {
                        return service.requestSMSCode(userId, phone);
                    }
                } else {
                    //谷歌验证码验证失败
                    return new Result(false, "验证码获取失败，请稍候重试", null, null);

                }
            } else {
                //谷歌验证码验证失败
                return new Result(false, "验证码获取失败，请稍候重试", null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码获取失败，请稍候重试", null, null);
        }
    }

    @GetMapping("/RequestSMSCodeWithOutLogin")
    @ApiOperation("非登录状态下请求短信验证码，不会验证登录账号和申请手机账号是否相符")
    public Result requestSMSCodeWithOutLogin(HttpServletRequest request, String phone, String googleToken) {
        try {
            //先验证 recaptcha
            Request verifyRequest = new Request.Builder()
                    .url("https://www.recaptcha.net/recaptcha/api/siteverify?secret=" + Constant.RECAPTCHA_SECRET + "&response=" + googleToken)
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .writeTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .build();
            Response response = client.newCall(verifyRequest).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String json = responseBody.string();
                RecaptchaBean recaptchaBean = new Gson().fromJson(json, RecaptchaBean.class);
                //谷歌验证码验证成功
                if (recaptchaBean.getSuccess()) {
                    return service.requestSMSCode(phone);
                } else {
                    //谷歌验证码验证失败
                    return new Result(false, "验证码获取失败，请稍候重试", null, null);

                }
            } else {
                //谷歌验证码验证失败
                return new Result(false, "验证码获取失败，请稍候重试", null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码获取失败，请稍候重试", null, null);
        }
    }

    @GetMapping("/VerifySMSCode")
    @ApiOperation("验证短信验证码")
    public Result verifySMSCode(String phone, String code, String codeStamp) {
        return service.verifySMSCode(phone, code, codeStamp);
    }

    @GetMapping("/RegisterRequestSMSCode")
    @ApiOperation("注册时请求验证码")
    public Result registerRequestSMSCode(String phone, String token) {
        try {
            return service.registerRequestSMSCode(phone, token);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码请求出错", null, null);
        }
    }

    @GetMapping("/ResetPasswordRequestSMSCode")
    @ApiOperation("注册时请求验证码")
    public Result resetPasswordRequestSMSCode(String phone, String token) {
        try {
            return service.resetPasswordRequestSMSCode(phone, token);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码请求出错", null, null);
        }
    }

    @PostMapping("/ThirdPartyLoginBindPhone")
    @ApiOperation("第三方登录绑定手机号")
    public Result thirdPartyLoginBindPhone(HttpServletRequest request, @RequestBody UserBean userBean) {
        String phone = request.getHeader("Phone");
        String code = request.getHeader("Code");
        String stamp = request.getHeader("CodeStamp");
        Result result = service.verifySMSCode(phone, code, stamp);
        if (result.getResult()) {
            userBean.setUserPhone(phone);
            return service.addUser(userBean);
        }
        return new Result(false, "登录出错，请检查后重试", null, null);
    }
}
