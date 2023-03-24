package com.lixyz.lifekeeper.service;

import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.Overview;
import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.bill.billaccount.BillAccount;
import com.lixyz.lifekeeper.bean.bill.billcategory.BillCategory;
import com.lixyz.lifekeeper.bean.sms.SMSBean;
import com.lixyz.lifekeeper.bean.sms.SMSResponseBean;
import com.lixyz.lifekeeper.bean.sms.yotest.YoTestVerifyRespBean;
import com.lixyz.lifekeeper.bean.user.UserBean;
import com.lixyz.lifekeeper.dao.UserDao;
import com.lixyz.lifekeeper.util.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final UserDao userDao;


    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 插入用户
     */
    @Transactional(rollbackFor = Exception.class)
    public Result addUser(UserBean user) {
        try {
            addBillCategoryAndBillAccount(user.getUserId(), user.getCreateTime());
            if (user.getUserIconUrl() != null) {
                if (user.getUserIconUrl().startsWith("http://") || user.getUserIconUrl().startsWith("https://")) {
                    downUserIcon(user.getUserIconUrl(), user.getUserId());
                    user.setUserIconUrl(user.getUserId() + ".png");
                }
            }
            int addResult = userDao.insertUser(user);
            if (addResult > 0) {
                return new Result(true, null, null, true);
            } else {
                return new Result(false, null, null, false);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    public void downUserIcon(String url, String userId) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().retryOnConnectionFailure(false)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            InputStream in = responseBody.byteStream();
            FileOutputStream fos = new FileOutputStream("/files/LifeKeeperUserIcon/" + userId + ".png");
            byte[] arr = new byte[1024];
            int len;
            while ((len = in.read(arr)) != -1) {
                fos.write(arr, 0, len);
                fos.flush();
            }
            fos.close();
            in.close();
        }
    }


    private void addBillCategoryAndBillAccount(String userId, long userAddTime) throws SQLException {
        String[] incomeCategories = {"薪水", "奖金", "理财"};
        String[] expendCategories = {"房租", "房贷", "出行", "购物", "美食"};
        String[] accounts = {"银行卡", "现金", "支付宝", "微信"};
        List<BillCategory> billCategoryList = new ArrayList<>();
        int categoryIndex = 0;
        for (String name : incomeCategories) {
            BillCategory category = new BillCategory();
            category.setObjectId(StringUtil.getRandomString());
            category.setCategoryId(StringUtil.getRandomString());
            category.setCategoryUser(userId);
            category.setCategoryName(name);
            category.setIsIncome(1);
            category.setCategoryStatus(1);
            category.setCategoryType(0);
            category.setCreateTime(userAddTime);
            category.setUpdateTime(0);
            category.setOrderIndex(categoryIndex);
            categoryIndex++;
            billCategoryList.add(category);
        }
        for (String name : expendCategories) {
            BillCategory category = new BillCategory();
            category.setObjectId(StringUtil.getRandomString());
            category.setCategoryId(StringUtil.getRandomString());
            category.setCategoryUser(userId);
            category.setCategoryName(name);
            category.setIsIncome(-1);
            category.setCategoryStatus(1);
            category.setCategoryType(0);
            category.setCreateTime(userAddTime);
            category.setUpdateTime(0);
            category.setOrderIndex(categoryIndex);
            categoryIndex++;
            billCategoryList.add(category);
        }
        userDao.addBillCategory(billCategoryList);
        List<BillAccount> billAccountList = new ArrayList<>();
        int accountIndex = 0;
        for (String name : accounts) {
            BillAccount account = new BillAccount();
            account.setObjectId(StringUtil.getRandomString());
            account.setAccountId(StringUtil.getRandomString());
            account.setAccountUser(userId);
            account.setAccountName(name);
            account.setAccountStatus(1);
            account.setAccountType(0);
            account.setCreateTime(userAddTime);
            account.setUpdateTime(0);
            account.setOrderIndex(accountIndex);
            accountIndex++;
            billAccountList.add(account);
        }
        userDao.addBillAccount(billAccountList);
    }

    /**
     * 删除用户
     */
    public Result deleteUser(String objectId, long updateTime) {
        try {
            int deleteResult = userDao.deleteUser(objectId, updateTime);
            if (deleteResult > 0) {
                return new Result(true, null, null, true);
            } else {
                return new Result(true, null, null, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    /**
     * 根据用户手机和密码查找用户
     */
    public Result selectUserByUserPhoneAndPassword(String phone, String password) {
        try {
            long count = userDao.phoneIsRegistered(phone);
            if (count > 0) {
                UserBean userBean = userDao.selectUserByPhoneAndPassword(phone, password);
                if (userBean == null) {
                    return new Result(false, "帐号密码不匹配,请检查后重试", null, null);
                } else {
                    return new Result(true, null, null, userBean);
                }
            } else {
                return new Result(false, "帐号密码不匹配,请检查后重试", null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    /**
     * 手机号是否已经注册过
     */
    public Result phoneIsRegistered(String phone) {
        try {
            long count = userDao.phoneIsRegistered(phone);
            if (count > 0) {
                return new Result(true, null, null, true);
            } else {
                return new Result(false, null, null, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    /**
     * 根据 weibo ID 查找用户
     */
    public Result selectUserByWeiboId(String userBindWeiboId) {
        try {
            UserBean userBean = userDao.selectUserByWeiboId(userBindWeiboId);
            if (userBean == null) {
                return new Result(false, null, null, null);
            } else {
                return new Result(true, null, null, userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    /**
     * 根据 QQ ID 查找用户
     */
    public Result selectUserByQQId(String userBindQQId) {
        try {
            UserBean userBean = userDao.selectUserByQQId(userBindQQId);
            if (userBean == null) {
                return new Result(false, null, null, null);
            } else {
                return new Result(true, null, null, userBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    private Overview getOverviewByUserId(UserBean userBean) throws SQLException {
        long monthStart = TimeUtil.getCurrentMonthStart();
        long monthEnd = TimeUtil.getCurrentMonthEnd();
        long dayStart = TimeUtil.getTodayStart();
        long dayEnd = TimeUtil.getTodayEnd();
        Overview overview = new Overview();
        overview.setUserBean(userBean);
        overview.setIncomeCount(userDao.getCurrentMonthIncomeCount(userBean.getUserId(), monthStart, monthEnd));
        overview.setExpendCount(userDao.getCurrentMonthExpendCount(userBean.getUserId(), monthStart, monthEnd));
        overview.setPlanCountOfDay(userDao.getPlayCount(userBean.getUserId(), dayStart, dayEnd));
        overview.setPlanCountOfMonth(userDao.getPlayCount(userBean.getUserId(), monthStart, monthEnd));
        overview.setFileCount((int) (userDao.queryImageCountByUserId(userBean.getUserId()) + userDao.queryRecordCountByUserId(userBean.getUserId()) + userDao.queryVideoCount(userBean.getUserId())));
        return overview;
    }


    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public Result updateUser(long updateTime, String objectId, UserBean user) {
        try {
            int updateResult = userDao.updateUser(updateTime, objectId);
            int addResult = userDao.insertUser(user);
            if (updateResult == addResult) {
                return new Result(true, null, null, true);
            } else {
                return new Result(true, null, null, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错啦，请稍候重试", e, null);
        }
    }

    /**
     * 上传头像
     *
     * @param file 图片文件夹
     * @return 上传图片原型
     */
    public Result uploadImg(String userId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new Result(false, "文件为空，请检查后重试", new Exception("文件为空"), null);
            }
            //创建目录
            File dir = new File("/files/LifeKeeperUserIcon/");
            if (!dir.exists()) {
                boolean mkdirResult = dir.mkdirs();
                if (!mkdirResult) {
                    return new Result(false, "服务器出错，请稍候重试", new Exception("创建用户头像目录出错"), null);
                }
            }
            String fileFormatName = FileUtil.getFileFormat(file.getOriginalFilename());
            //随机生成文件名
            String fileName = UUID.randomUUID() + "." + fileFormatName;
            File dest = new File("/files/LifeKeeperUserIcon/" + fileName);
            File webpFile = new File("/files/LifeKeeperUserIcon/" + UUID.randomUUID() + ".webp");
            FileUtil.multipartFileTransferToFile(file, dest);
            BufferedImage bufferedImage = ImageIO.read(dest);
            boolean webp = ImageIO.write(bufferedImage, "webp", webpFile);
            if (webp) {
                int result = userDao.updateUserIcon(userId, webpFile.getName());
                if (result > 0) {
                    return new Result(true, null, null, webpFile.getName());
                } else {
                    return new Result(false, "数据库更新出错，请稍候重试", null, null);
                }
            } else {
                return new Result(false, "格式化 webp 出错，请稍候重试", null, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e.getMessage(), null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result resetPassword(String userId, String phone, String code, String codeStamp, String newPassword) throws SQLException {
        //先检查验证码
        Result result = verifySMSCode(phone, code, codeStamp);
        if (result.getResult()) {
            UserBean userBean = userDao.checkUserIsExists(userId, phone);
            if (userBean == null) {
                return new Result(false, "当前用户绑定的不是这个手机", null, false);
            } else {
                int updateResult = userDao.updateUser(System.currentTimeMillis(), userBean.getObjectId());
                if (updateResult > 0) {
                    userBean.setObjectId(StringUtil.getRandomString());
                    userBean.setUserPassword(newPassword);
                    userBean.setCreateTime(System.currentTimeMillis());
                    int addResult = userDao.insertUser(userBean);
                    if (addResult > 0) {
                        return new Result(true, "密码修改失败，请稍候重试", null, null);
                    } else {
                        return new Result(false, "密码修改失败，请稍候重试", null, null);
                    }
                } else {
                    return new Result(false, "密码修改失败，请稍候重试", null, null);
                }
            }
        } else {
            return new Result(false, "验证码不通过", null, null);
        }
    }

    public Result resetPassword(String phone, String password) throws SQLException {
        int count = userDao.resetPassword(phone, password);
        if (count > 0) {
            return new Result(true, null, null, null);
        } else {
            return new Result(false, null, null, null);
        }
    }

    public Result phoneIsBindWeibo(String phone) {
        try {
            UserBean userBean = userDao.checkPhoneBindWeibo(phone);
            return new Result(true, null, null, userBean != null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result phoneIsBindQQ(String phone) {
        try {
            UserBean userBean = userDao.checkPhoneBindQQ(phone);
            return new Result(true, null, null, userBean != null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result bindWeibo(UserBean userBean) {
        try {
            //先查看该手机号有没有注册/绑定过
            String userPhone = userBean.getUserPhone();
            UserBean existsUser = userDao.selectUserByPhone(userPhone);
            if (existsUser != null) {
                existsUser.setUserBindWeibo(userBean.getUserBindWeibo());
                existsUser.setUserBindWeiboAccessToken(userBean.getUserBindWeiboAccessToken());
                existsUser.setUserBindWeiboIcon(userBean.getUserBindWeiboIcon());
                existsUser.setUserBindWeiboId(userBean.getUserBindWeiboId());
                existsUser.setUserBindWeiboExpiresTime(userBean.getUserBindWeiboExpiresTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result requestSMSCode(String userId, String phone) throws Exception {
        //验证该用户是否存在
        UserBean user = userDao.checkUserIsExists(userId, phone);
        if (user == null) {
            return new Result(false, "当前用户绑定的不是这个手机号", null, null);
        } else {
            String randomNum = StringUtil.getRandomNumber(6);
            long currTime = System.currentTimeMillis();
            SMSBean bean = new SMSBean();
            bean.setObjectId(UUID.randomUUID().toString());
            bean.setSMSId(UUID.randomUUID().toString());
            bean.setPhoneNumber(phone);
            bean.setSMSCode(randomNum);
            bean.setSMSStatus(1);
            bean.setSMSType(0);
            bean.setCreateTime(currTime);
            bean.setVerifyTime(0);
            bean.setPositionNum(currTime);
            bean.setUserId(userId);
            int i = userDao.addSmsCode(bean);
            if (i > 0) {
                Credential cred = new Credential(Constant.SMS_SECRET_ID, Constant.SMS_SECRET_KEY);
                // 实例化一个http选项，可选的，没有特殊需求可以跳过
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("sms.tencentcloudapi.com");
                // 实例化一个client选项，可选的，没有特殊需求可以跳过
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);
                // 实例化要请求产品的client对象,clientProfile是可选的
                SmsClient client = new SmsClient(cred, "ap-beijing", clientProfile);
                // 实例化一个请求对象,每个接口都会对应一个request对象
                SendSmsRequest req = new SendSmsRequest();
                String[] phoneNumberSet1 = {"+86" + phone};
                req.setPhoneNumberSet(phoneNumberSet1);
                req.setSmsSdkAppId("1400650636");
                req.setSignName("跟着lixyz学Java");
                req.setTemplateId("1698702");
                String[] templateParamSet1 = {randomNum, "5"};
                req.setTemplateParamSet(templateParamSet1);
                // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
                SendSmsResponse resp = client.SendSms(req);
                // 输出json格式的字符串回包
                SMSResponseBean sMSResponseBean = new Gson().fromJson(SendSmsResponse.toJsonString(resp), SMSResponseBean.class);
                if ("Ok".equals(sMSResponseBean.getSendStatusSet()[0].getCode())) {
                    return new Result(true, "短信发送成功", null, "" + currTime);
                } else {
                    System.out.println(SendSmsResponse.toJsonString(resp));
                    throw new Exception("短信发送失败");
                }
            }
            return new Result(false, null, null, null);
        }
    }

    public Result resetClientId(String userId, String clientId) {
        int count = userDao.resetClientId(userId, clientId);
        if (count > 0) {
            return new Result(true, null, null, null);
        } else {
            int result = userDao.addPushClient(userId, clientId);
            return new Result(result > 0, null, null, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result requestSMSCode(String phone) {
        try {
            String randomNum = StringUtil.getRandomNumber(6);
            long currTime = System.currentTimeMillis();
            SMSBean bean = new SMSBean();
            bean.setObjectId(UUID.randomUUID().toString());
            bean.setSMSId(UUID.randomUUID().toString());
            bean.setPhoneNumber(phone);
            bean.setSMSCode(randomNum);
            bean.setSMSStatus(1);
            bean.setSMSType(0);
            bean.setCreateTime(currTime);
            bean.setVerifyTime(0);
            bean.setPositionNum(currTime);
            bean.setUserId(null);
            int i = userDao.addSmsCode(bean);
            if (i > 0) {
                Credential cred = new Credential(Constant.SMS_SECRET_ID, Constant.SMS_SECRET_KEY);
                // 实例化一个http选项，可选的，没有特殊需求可以跳过
                HttpProfile httpProfile = new HttpProfile();
                httpProfile.setEndpoint("sms.tencentcloudapi.com");
                // 实例化一个client选项，可选的，没有特殊需求可以跳过
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setHttpProfile(httpProfile);
                // 实例化要请求产品的client对象,clientProfile是可选的
                SmsClient client = new SmsClient(cred, "ap-beijing", clientProfile);
                // 实例化一个请求对象,每个接口都会对应一个request对象
                SendSmsRequest req = new SendSmsRequest();
                String[] phoneNumberSet1 = {"+86" + phone};
                req.setPhoneNumberSet(phoneNumberSet1);
                req.setSmsSdkAppId("1400650636");
                req.setSignName("跟着lixyz学Java");
                req.setTemplateId("1698702");
                String[] templateParamSet1 = {randomNum, "5"};
                req.setTemplateParamSet(templateParamSet1);
                // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
                SendSmsResponse resp = client.SendSms(req);
                // 输出json格式的字符串回包
                SMSResponseBean sMSResponseBean = new Gson().fromJson(SendSmsResponse.toJsonString(resp), SMSResponseBean.class);
                if ("Ok".equals(sMSResponseBean.getSendStatusSet()[0].getCode())) {
                    return new Result(true, "短信发送成功", null, "" + currTime);
                } else {
                    System.out.println(SendSmsResponse.toJsonString(resp));
                }
            }
            return new Result(false, null, null, null);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "请求短信失败", null, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result verifySMSCode(String phone, String code, String codeStamp) {
        int count = userDao.verifySMSCode(phone, code, codeStamp);
        if (count > 0) {
            int result = userDao.failSMSCode(phone, code, codeStamp, System.currentTimeMillis());
            if (result > 0) {
                return new Result(true, null, null, null);
            }
        }
        return new Result(false, null, null, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result resetPhone(String userId, String phone, String code, String codeStamp) throws SQLException {
        long l = userDao.phoneIsRegistered(phone);
        if (l > 0) {
            return new Result(false, "该手机号已经注册过了", null, null);
        } else {
            Result result = verifySMSCode(phone, code, codeStamp);
            if (result.getResult()) {
                UserBean userBean = userDao.getUserByUserId(userId);
                if (userBean == null) {
                    return new Result(false, "修改失败，请检查登录状态", null, null);
                } else {
                    int i = userDao.updateUser(System.currentTimeMillis(), userBean.getObjectId());
                    if (i > 0) {
                        userBean.setObjectId(UUID.randomUUID().toString());
                        userBean.setUserPhone(phone);
                        userBean.setCreateTime(System.currentTimeMillis());
                        int addResult = userDao.insertUser(userBean);
                        if (addResult > 0) {
                            return new Result(true, null, null, null);
                        } else {
                            return new Result(false, "密码修改失败，请稍候重试", null, null);
                        }
                    } else {
                        return new Result(false, "密码修改失败，请稍候重试", null, null);
                    }
                }
            } else {
                return new Result(false, "验证码出错", null, null);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result registerRequestSMSCode(String phone, String token) throws Exception {
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("accessId", Constant.FAST_YO_TEST_ACCESS_ID);
        data.put("accessKey", Constant.FAST_YO_TEST_ACCESS_KEY);
        Gson gson = new Gson();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(gson.toJson(data), mediaType);

        Request request = new Request.Builder()
                .url("https://api.fastyotest.com/api/validate")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody resp = response.body();
        if (resp != null) {
            YoTestVerifyRespBean bean = gson.fromJson(resp.string(), YoTestVerifyRespBean.class);
            if ("success".equals(bean.getMsg())) {
                if (bean.getData().getStatus() == 1 && bean.getData().getScore() >= 60) {
                    long count = userDao.phoneIsRegistered(phone);
                    if (count > 0) {
                        return new Result(false, "该手机号已经注册过了", null, null);
                    } else {
                        String randomNum = StringUtil.getRandomNumber(6);
                        long currTime = System.currentTimeMillis();
                        SMSBean smsBean = new SMSBean();
                        smsBean.setObjectId(UUID.randomUUID().toString());
                        smsBean.setSMSId(UUID.randomUUID().toString());
                        smsBean.setPhoneNumber(phone);
                        smsBean.setSMSCode(randomNum);
                        smsBean.setSMSStatus(1);
                        smsBean.setSMSType(0);
                        smsBean.setCreateTime(currTime);
                        smsBean.setVerifyTime(0);
                        smsBean.setPositionNum(currTime);
                        smsBean.setUserId(null);
                        int i = userDao.addSmsCode(smsBean);
                        if (i > 0) {
                            Credential cred = new Credential(Constant.SMS_SECRET_ID, Constant.SMS_SECRET_KEY);
                            // 实例化一个http选项，可选的，没有特殊需求可以跳过
                            HttpProfile httpProfile = new HttpProfile();
                            httpProfile.setEndpoint("sms.tencentcloudapi.com");
                            // 实例化一个client选项，可选的，没有特殊需求可以跳过
                            ClientProfile clientProfile = new ClientProfile();
                            clientProfile.setHttpProfile(httpProfile);
                            // 实例化要请求产品的client对象,clientProfile是可选的
                            SmsClient client = new SmsClient(cred, "ap-beijing", clientProfile);
                            // 实例化一个请求对象,每个接口都会对应一个request对象
                            SendSmsRequest req = new SendSmsRequest();
                            String[] phoneNumberSet1 = {"+86" + phone};
                            req.setPhoneNumberSet(phoneNumberSet1);
                            req.setSmsSdkAppId("1400650636");
                            req.setSignName("跟着lixyz学Java");
                            req.setTemplateId("1698702");
                            String[] templateParamSet1 = {randomNum, "5"};
                            req.setTemplateParamSet(templateParamSet1);
                            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
                            SendSmsResponse sendSmsResponse = client.SendSms(req);
                            // 输出json格式的字符串回包
                            SMSResponseBean sMSResponseBean = new Gson().fromJson(SendSmsResponse.toJsonString(sendSmsResponse), SMSResponseBean.class);
                            if ("Ok".equals(sMSResponseBean.getSendStatusSet()[0].getCode())) {
                                return new Result(true, "短信发送成功", null, "" + currTime);
                            } else {
                                System.out.println(SendSmsResponse.toJsonString(sendSmsResponse));
                                throw new Exception("短信发送失败");
                            }
                        }
                        return new Result(false, "短信验证码发送失败，请稍候重试", null, null);
                    }
                } else {
                    return new Result(false, "风控没有通过，请求验证码失败" + bean.getData().getStatus() + "|" + bean.getData().getScore(), null, null);
                }
            } else {
                return new Result(false, "验证码请求出错", null, null);
            }
        } else {
            return new Result(false, "短信验证码发送失败，请稍候重试", null, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result verifyCodeLogin(String phone, String code, String stamp) throws SQLException {
        Result result = verifySMSCode(phone, code, stamp);
        if (result.getResult()) {
            UserBean userBean = userDao.selectUserByPhone(phone);
            if (userBean == null) {
                UserBean user = new UserBean();
                user.setObjectId(UUID.randomUUID().toString());
                user.setUserId(UUID.randomUUID().toString());
                user.setUserName(phone);
                user.setUserPhone(phone);
                user.setUserPassword(null);
                user.setUserIconUrl("user_icon_default.webp");
                user.setUserBindWeibo(null);
                user.setUserBindWeiboAccessToken(null);
                user.setUserBindWeiboIcon(null);
                user.setUserBindWeiboExpiresTime(null);
                user.setUserBindWeiboId(null);
                user.setUserBindQQ(null);
                user.setUserBindQQOpenId(null);
                user.setUserBindQQExpiresTime(null);
                user.setUserBindQQAccessToken(null);
                user.setUserBindQQIcon(null);
                user.setUserStatus(1);
                user.setUserType(0);
                user.setCreateTime(System.currentTimeMillis());
                user.setUpdateTime(0);
                addBillCategoryAndBillAccount(user.getUserId(), user.getCreateTime());
                int addResult = userDao.insertUser(user);
                if (addResult > 0) {
                    return new Result(true, null, null, user);
                } else {
                    return new Result(false, null, null, null);
                }
            } else {
                return new Result(true, null, null, userBean);
            }
        } else {
            return new Result(false, null, null, null);
        }
    }

    public Result resetPasswordRequestSMSCode(String phone, String token) throws Exception {
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("accessId", Constant.FAST_YO_TEST_ACCESS_ID);
        data.put("accessKey", Constant.FAST_YO_TEST_ACCESS_KEY);
        Gson gson = new Gson();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(gson.toJson(data), mediaType);

        Request request = new Request.Builder()
                .url("https://api.fastyotest.com/api/validate")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody resp = response.body();
        if (resp != null) {
            YoTestVerifyRespBean bean = gson.fromJson(resp.string(), YoTestVerifyRespBean.class);
            if ("success".equals(bean.getMsg())) {
                if (bean.getData().getStatus() == 1 && bean.getData().getScore() >= 60) {
                    long count = userDao.phoneIsRegistered(phone);
                    if (count > 0) {
                        String randomNum = StringUtil.getRandomNumber(6);
                        long currTime = System.currentTimeMillis();
                        SMSBean smsBean = new SMSBean();
                        smsBean.setObjectId(UUID.randomUUID().toString());
                        smsBean.setSMSId(UUID.randomUUID().toString());
                        smsBean.setPhoneNumber(phone);
                        smsBean.setSMSCode(randomNum);
                        smsBean.setSMSStatus(1);
                        smsBean.setSMSType(0);
                        smsBean.setCreateTime(currTime);
                        smsBean.setVerifyTime(0);
                        smsBean.setPositionNum(currTime);
                        smsBean.setUserId(null);
                        int i = userDao.addSmsCode(smsBean);
                        if (i > 0) {
                            Credential cred = new Credential(Constant.SMS_SECRET_ID, Constant.SMS_SECRET_KEY);
                            // 实例化一个http选项，可选的，没有特殊需求可以跳过
                            HttpProfile httpProfile = new HttpProfile();
                            httpProfile.setEndpoint("sms.tencentcloudapi.com");
                            // 实例化一个client选项，可选的，没有特殊需求可以跳过
                            ClientProfile clientProfile = new ClientProfile();
                            clientProfile.setHttpProfile(httpProfile);
                            // 实例化要请求产品的client对象,clientProfile是可选的
                            SmsClient client = new SmsClient(cred, "ap-beijing", clientProfile);
                            // 实例化一个请求对象,每个接口都会对应一个request对象
                            SendSmsRequest req = new SendSmsRequest();
                            String[] phoneNumberSet1 = {"+86" + phone};
                            req.setPhoneNumberSet(phoneNumberSet1);
                            req.setSmsSdkAppId("1400650636");
                            req.setSignName("跟着lixyz学Java");
                            req.setTemplateId("1698702");
                            String[] templateParamSet1 = {randomNum, "5"};
                            req.setTemplateParamSet(templateParamSet1);
                            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
                            SendSmsResponse sendSmsResponse = client.SendSms(req);
                            // 输出json格式的字符串回包
                            SMSResponseBean sMSResponseBean = new Gson().fromJson(SendSmsResponse.toJsonString(sendSmsResponse), SMSResponseBean.class);
                            if ("Ok".equals(sMSResponseBean.getSendStatusSet()[0].getCode())) {
                                return new Result(true, "短信发送成功", null, "" + currTime);
                            } else {
                                System.out.println(SendSmsResponse.toJsonString(sendSmsResponse));
                                throw new Exception("短信发送失败");
                            }
                        }
                        return new Result(false, "获取验证码出错，请稍候重试", null, null);
                    } else {
                        return new Result(false, "该手机号没有注册过", null, null);
                    }
                } else {
                    return new Result(false, "风控没有通过，请求验证码失败" + bean.getData().getStatus() + "|" + bean.getData().getScore(), null, null);
                }
            } else {
                return new Result(false, "验证码请求出错", null, null);
            }
        } else {
            return new Result(false, "短信验证码发送失败，请稍候重试", null, null);
        }
    }
}
