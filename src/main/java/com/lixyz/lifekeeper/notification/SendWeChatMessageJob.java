package com.lixyz.lifekeeper.notification;

import com.google.gson.Gson;
import com.lixyz.lifekeeper.bean.getui.*;
import com.lixyz.lifekeeper.bean.plan.PlanBean;
import com.lixyz.lifekeeper.dao.PlanDao;
import com.lixyz.lifekeeper.notification.pojo.*;
import okhttp3.*;
import org.quartz.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cn.hutool.crypto.SecureUtil.sha256;

public class SendWeChatMessageJob implements Job {

    private final PlanDao dao;

    public SendWeChatMessageJob(PlanDao dao) {
        this.dao = dao;
    }

    private final Gson gson = new Gson();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            JobDetail detail = jobExecutionContext.getJobDetail();
            JobDataMap map = detail.getJobDataMap();
            PlanBean bean = (PlanBean) map.get("Message");
            String clientId = dao.getClientId(bean.getPlanUser());
            if (clientId != null) {
                String token = getAuthToken();
                sendMessage(token, clientId, bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String token, String clientId, PlanBean bean) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .build();
        PushBean pushBean = new PushBean();
        pushBean.setRequest_id(UUID.randomUUID().toString());

        Settings settings = new Settings(3 * 24 * 3600 * 1000);
        pushBean.setSettings(settings);

        Audience audience = new Audience(new String[]{clientId});
        pushBean.setAudience(audience);
        PushMessage pushMessage = new PushMessage();
        Notification notification = new Notification();

        notification.setTitle(bean.getPlanName());

        if (bean.getAlarmTime() == 0) {
            notification.setBody("您有一个任务要完成:" + bean.getPlanName());
        } else {
            notification.setBody(bean.getAlarmTime() + "分钟后，您有一个任务要完成:" + bean.getPlanName());
        }

        notification.setClick_type("intent");
        notification.setIntent("intent://com.lixyz.lifekeeperforkotlin/planWeb?#Intent;scheme=lifekeeperscheme;launchFlags=0x4000000;package=com.lixyz.lifekeeperforkotlin;component=com.lixyz.lifekeeperforkotlin/.view.activity.PlanWebActivity;S.gttask=;S.payload=payloadStr;end");

        pushMessage.setNotification(notification);
        pushBean.setPush_message(pushMessage);
        RequestBody pushBody = RequestBody.create(new Gson().toJson(pushBean), okhttp3.MediaType.parse("application/json;charset=UTF-8"));
        Request pushRequest = new Request.Builder()
                .url("https://restapi.getui.com/v2/vKAmN4echS6pKD3NqOlrG7/push/single/cid")
                .addHeader("content-type", "application/json;charset=utf-8")
                .addHeader("token", token)
                .post(pushBody)
                .build();
        client.newCall(pushRequest).execute();
    }

    private String getAuthToken() throws IOException {
        long time = System.currentTimeMillis();
        String sign = sha256("V2pN3LnwNd9KhN7DPbgZD9" + time + "gK5U2ikzUeAlw2X2EVbWf7");
        AuthBean bean = new AuthBean();
        bean.setSign(sign);
        bean.setTimestamp(time + "");
        bean.setAppkey("V2pN3LnwNd9KhN7DPbgZD9");

        RequestBody body = RequestBody.create(new Gson().toJson(bean), okhttp3.MediaType.parse("application/json;charset=UTF-8"));
        Request autoRequest = new Request.Builder()
                .url("https://restapi.getui.com/v2/vKAmN4echS6pKD3NqOlrG7/auth")
                .addHeader("content-type", "application/json;charset=utf-8")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .build();
        Response authResponse = client.newCall(autoRequest).execute();
        ResponseBody authResponseBody = authResponse.body();
        if (authResponseBody != null) {
            String json = authResponseBody.string();
            AuthResponseBean authResponseBean = gson.fromJson(json, AuthResponseBean.class);
            if (authResponseBean.getCode() == 0) {
                return authResponseBean.getData().getToken();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * PlanBean 转为微信通知消息实体类
     *
     * @param planBean PlanBean
     * @return 微信通知消息对象
     */
    private NotificationMessageBean getNotificationMessage(PlanBean planBean) {
        NotificationMessageBean bean = new NotificationMessageBean();
        Data data = new Data();
        //计划名称
        PlanName planName = new PlanName();
        planName.setValue(planBean.getPlanName());
        //计划时间
        PlanTime planTime = new PlanTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(planBean.getStartTime() - planBean.getAlarmTime());
        planTime.setValue(format.format(date));
        //计划描述
        PlanDescription planDescription = new PlanDescription();
        if (planBean.getPlanDescription() == null || planBean.getPlanDescription().length() == 0) {
            planDescription.setValue("无描述");
        } else {
            planDescription.setValue(planBean.getPlanDescription());
        }
        //计划地点
        PlanLocation planLocation = new PlanLocation();
        if (planBean.getPlanLocation() == null || planBean.getPlanLocation().length() == 0) {
            planLocation.setValue("未设置地点");
        } else {
            planLocation.setValue(planBean.getPlanLocation());
        }
        data.setPlanName(planName);
        data.setPlanTime(planTime);
        data.setPlanDescription(planDescription);
        data.setPlanLocation(planLocation);
        bean.setData(data);
        return bean;
    }
}
