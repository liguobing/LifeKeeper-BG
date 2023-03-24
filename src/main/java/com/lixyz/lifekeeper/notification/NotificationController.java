package com.lixyz.lifekeeper.notification;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.plan.PlanBean;
import io.swagger.annotations.Api;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "通知相关接口")
public class NotificationController {

    private final Scheduler scheduler;


    public NotificationController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @RequestMapping("/GetNotification")
    public Result getNotification() {
        try {
            ArrayList<NotificationBean> list = new ArrayList<>();
            List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
            for (String groupName : triggerGroupNames) {
                //组装group的匹配，为了模糊获取所有的triggerKey或者jobKey
                GroupMatcher groupMatcher = GroupMatcher.groupEquals(groupName);
                //获取所有的triggerKey
                Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);
                for (TriggerKey triggerKey : triggerKeySet) {
                    //通过triggerKey在scheduler中获取trigger对象
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    //获取trigger拥有的Job
                    JobKey jobKey = trigger.getJobKey();
                    JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
                    Date startTime = trigger.getStartTime();
                    JobDataMap map = jobDetail.getJobDataMap();
                    PlanBean planBean = (PlanBean) map.get("Message");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String objectId = triggerKey.getName();
                    String groupId = triggerKey.getGroup();
                    String planName = planBean.getPlanName();
                    long time = startTime.getTime();
                    String formatTime = format.format(startTime);
                    NotificationBean bean = new NotificationBean();
                    bean.setObjectId(objectId);
                    bean.setGroupId(groupId);
                    bean.setTime(time);
                    bean.setFormatTime(formatTime);
                    bean.setPlanName(planName);
                    list.add(bean);
                }
            }
            return new Result(true,null,null,list);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return new Result(false,null,null,null);
        }
    }
}
