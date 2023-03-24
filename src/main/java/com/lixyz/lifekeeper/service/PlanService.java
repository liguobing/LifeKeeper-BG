package com.lixyz.lifekeeper.service;

import com.lixyz.lifekeeper.bean.Result;
import com.lixyz.lifekeeper.bean.plan.*;
import com.lixyz.lifekeeper.dao.PlanDao;
import com.lixyz.lifekeeper.notification.NotificationBean;
import com.lixyz.lifekeeper.notification.SendWeChatMessageJob;
import com.lixyz.lifekeeper.util.StringUtil;
import com.lixyz.lifekeeper.util.TimeUtil;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

@Service
public class PlanService {
    private final PlanDao dao;

    private final Scheduler scheduler;

    public PlanService(PlanDao dao, Scheduler scheduler) {
        this.dao = dao;
        this.scheduler = scheduler;
    }


    @Transactional(rollbackFor = Exception.class)
    public Result addPlan(PlanRule planRule) {
        try {
            //保存计划规则
            int result = dao.addPlanRule(planRule);
            if (result > 0) {
                ArrayList<PlanBean> list;
                //开始保存计划
                //先看重复模式：
                switch (planRule.getRepeatType()) {
                    case 1:
                        list = buildOncePlan(planRule);
                        break;
                    case 2:
                        list = buildDailyPlan(planRule);
                        break;
                    case 3:
                        list = buildWeekPlan(planRule);
                        break;
                    case 4:
                        list = buildMonthPlan(planRule);
                        break;
                    case 5:
                        list = buildYearPlan(planRule);
                        break;
                    default:
                        list = new ArrayList<>();
                }
                for (PlanBean plan : list) {
                    if (plan.getAlarmTime() >= 0) {
                        ArrayList<PlanAlarm> alarms = createAlarm(plan);
                        addNotification(plan, alarms);
                    }
                }
                int count = dao.addPlan(list);
                if (count == list.size()) {
                    return new Result(true, null, null, null);
                } else {
                    return new Result(false, "服务器出错1", null, null);
                }
            } else {
                return new Result(false, "服务器出错2", null, null);
            }
        } catch (SchedulerException | SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错3", e, null);
        }
    }

    private ArrayList<PlanAlarm> createAlarm(PlanBean plan) throws SQLException {
        ArrayList<PlanAlarm> alarms = new ArrayList<>();
        long executeTime = plan.getStartTime() - plan.getAlarmTime() * 60000L;
        if (plan.getIsAllDay() > 0) {//全天计划，从开始时间到晚上10点，每隔小时提醒一次
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(executeTime);
            c.set(Calendar.HOUR_OF_DAY, 22);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long twentyOnClick = c.getTimeInMillis();
            while (executeTime <= twentyOnClick) {
                PlanAlarm alarm = new PlanAlarm();
                alarm.setObjectId(StringUtil.getRandomString());
                alarm.setGroupId(plan.getPlanId());
                alarm.setPlanId(plan.getPlanId());
                alarm.setPlanName(plan.getPlanName());
                alarm.setAlarmExecuteTime(executeTime);
                alarm.setAlarmStatus(1);
                alarm.setAlarmType(0);
                alarm.setCreateTime(plan.getCreateTime());
                alarm.setUpdateTime(0);
                alarms.add(alarm);
                dao.addPlanAlarm(alarm);
                executeTime += 3600000L;
            }
        } else {
            PlanAlarm alarm = new PlanAlarm();
            alarm.setObjectId(StringUtil.getRandomString());
            alarm.setGroupId(plan.getPlanId());
            alarm.setPlanId(plan.getPlanId());
            alarm.setPlanName(plan.getPlanName());
            alarm.setAlarmExecuteTime(executeTime);
            alarm.setAlarmStatus(1);
            alarm.setAlarmType(0);
            alarm.setCreateTime(plan.getCreateTime());
            alarm.setUpdateTime(0);
            alarms.add(alarm);
            dao.addPlanAlarm(alarm);
        }
        return alarms;
    }

    /**
     * quartz 添加提醒计划
     */
    private void addNotification(PlanBean plan, ArrayList<PlanAlarm> alarms) throws SchedulerException, SQLException {
        long currTime = System.currentTimeMillis();
        for (PlanAlarm alarm : alarms) {
            if (currTime > alarm.getAlarmExecuteTime()) {
                continue;
            }
            JobDetail job = JobBuilder.newJob(SendWeChatMessageJob.class).build();
            JobDataMap map = job.getJobDataMap();
            map.put("Message", plan);
            Trigger trigger = TriggerBuilder.newTrigger().
                    startAt(new Date(alarm.getAlarmExecuteTime())).
                    withIdentity(TriggerKey.triggerKey(alarm.getObjectId(), alarm.getGroupId())).
                    build();
            scheduler.scheduleJob(job, trigger);
        }
    }

    private ArrayList<PlanBean> buildYearPlan(PlanRule planRule) {
        ArrayList<PlanBean> list = new ArrayList<>();
        String groupId = StringUtil.getRandomString();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(planRule.getStartTime());
        if (planRule.getEndRepeatType() == 1) {
            long endTime = planRule.getEndRepeatValue();
            long lastExecuteTime = 0;
            int count = 0;
            while (true) {
                long executeTime = c.getTimeInMillis();
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(executeTime);
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(count + 1);
                planBean.setRepeatCount(count);
                planBean.setEndRepeatTime(c.getTimeInMillis());
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                if (executeTime > endTime) {
                    break;
                }
                lastExecuteTime = c.getTimeInMillis();
                count++;
                list.add(planBean);
                c.add(Calendar.YEAR, 1);
            }
            for (PlanBean bean : list) {
                bean.setRepeatCount(count);
                bean.setEndRepeatTime(lastExecuteTime);
            }
        } else if (planRule.getEndRepeatType() == 2) {

            long lastExecuteTime = 0;
            for (int i = 0; i < planRule.getEndRepeatValue(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(planRule.getStartTime());
                calendar.add(Calendar.YEAR, i);
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(calendar.getTimeInMillis());
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(i + 1);
                planBean.setRepeatCount((int) planRule.getEndRepeatValue());
                planBean.setEndRepeatTime(0);
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                list.add(planBean);
                lastExecuteTime = calendar.getTimeInMillis();
            }
            for (PlanBean plan : list) {
                plan.setEndRepeatTime(lastExecuteTime);
            }
        }
        return list;
    }

    private ArrayList<PlanBean> buildMonthPlan(PlanRule planRule) {
        ArrayList<PlanBean> list = new ArrayList<>();
        String groupId = StringUtil.getRandomString();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(planRule.getStartTime());
        if (planRule.getEndRepeatType() == 1) {
            long endTime = planRule.getEndRepeatValue();
            long lastExecuteTime = 0;
            int count = 0;
            while (true) {
                long executeTime = c.getTimeInMillis();
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(executeTime);
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(count + 1);
                planBean.setRepeatCount(count);
                planBean.setEndRepeatTime(c.getTimeInMillis());
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                if (executeTime > endTime) {
                    break;
                }
                lastExecuteTime = c.getTimeInMillis();
                count++;
                list.add(planBean);
                c.add(Calendar.MONTH, 1);
            }
            for (PlanBean bean : list) {
                bean.setRepeatCount(count);
                bean.setEndRepeatTime(lastExecuteTime);
            }
        } else if (planRule.getEndRepeatType() == 2) {

            long lastExecuteTime = 0;
            for (int i = 0; i < planRule.getEndRepeatValue(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(planRule.getStartTime());
                calendar.add(Calendar.MONTH, i);
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(calendar.getTimeInMillis());
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(i + 1);
                planBean.setRepeatCount((int) planRule.getEndRepeatValue());
                planBean.setEndRepeatTime(0);
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                list.add(planBean);
                lastExecuteTime = calendar.getTimeInMillis();
            }
            for (PlanBean plan : list) {
                plan.setEndRepeatTime(lastExecuteTime);
            }
        }
        return list;
    }

    private ArrayList<PlanBean> buildWeekPlan(PlanRule planRule) {
        ArrayList<PlanBean> list = new ArrayList<>();
        String groupId = StringUtil.getRandomString();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(planRule.getStartTime());
        if (planRule.getEndRepeatType() == 1) {
            long endTime = planRule.getEndRepeatValue();
            long lastExecuteTime = 0;
            int count = 0;
            while (true) {
                long executeTime = c.getTimeInMillis();
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(executeTime);
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(count + 1);
                planBean.setRepeatCount(count);
                planBean.setEndRepeatTime(c.getTimeInMillis());
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                if (executeTime > endTime) {
                    break;
                }
                lastExecuteTime = c.getTimeInMillis();
                count++;
                list.add(planBean);
                c.add(Calendar.WEEK_OF_YEAR, 1);
            }
            for (PlanBean bean : list) {
                bean.setRepeatCount(count);
                bean.setEndRepeatTime(lastExecuteTime);
            }
        } else if (planRule.getEndRepeatType() == 2) {

            long lastExecuteTime = 0;
            for (int i = 0; i < planRule.getEndRepeatValue(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(planRule.getStartTime());
                calendar.add(Calendar.WEEK_OF_YEAR, i);
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(calendar.getTimeInMillis());
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(i + 1);
                planBean.setRepeatCount((int) planRule.getEndRepeatValue());
                planBean.setEndRepeatTime(0);
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                list.add(planBean);
                lastExecuteTime = calendar.getTimeInMillis();
            }
            for (PlanBean plan : list) {
                plan.setEndRepeatTime(lastExecuteTime);
            }
        }
        return list;
    }

    private ArrayList<PlanBean> buildDailyPlan(PlanRule planRule) {
        ArrayList<PlanBean> list = new ArrayList<>();
        String groupId = StringUtil.getRandomString();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(planRule.getStartTime());
        if (planRule.getEndRepeatType() == 1) {
            long endTime = planRule.getEndRepeatValue();
            long lastExecuteTime = 0;
            int count = 0;
            while (true) {
                long executeTime = c.getTimeInMillis();
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(executeTime);
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(count + 1);
                planBean.setRepeatCount(count);
                planBean.setEndRepeatTime(c.getTimeInMillis());
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                if (executeTime > endTime) {
                    break;
                }
                lastExecuteTime = c.getTimeInMillis();
                count++;
                list.add(planBean);
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
            for (PlanBean bean : list) {
                bean.setRepeatCount(count);
                bean.setEndRepeatTime(lastExecuteTime);
            }
        } else if (planRule.getEndRepeatType() == 2) {
            long lastExecuteTime = 0;
            for (int i = 0; i < planRule.getEndRepeatValue(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(planRule.getStartTime());
                calendar.add(Calendar.DAY_OF_YEAR, i);
                PlanBean planBean = new PlanBean();
                planBean.setObjectId(StringUtil.getRandomString());
                planBean.setPlanId(StringUtil.getRandomString());
                planBean.setGroupId(groupId);
                planBean.setIsAllDay(planRule.getIsAllDay());
                planBean.setPlanName(planRule.getPlanName());
                planBean.setPlanDescription(planRule.getPlanDescription());
                planBean.setPlanLocation(planRule.getPlanLocation());
                planBean.setPlanUser(planRule.getPlanUser());
                planBean.setStartTime(calendar.getTimeInMillis());
                planBean.setAlarmTime(planRule.getAlarmTime());
                planBean.setRepeatMode(planRule.getRepeatType());
                planBean.setRepeatIndex(i + 1);
                planBean.setRepeatCount((int) planRule.getEndRepeatValue());
                planBean.setEndRepeatTime(0);
                planBean.setIsFinished(-1);
                planBean.setPlanStatus(1);
                planBean.setPlanType(0);
                planBean.setCreateTime(planRule.getCreateTime());
                planBean.setUpdateTime(0);
                planBean.setUpdateTime(0);
                list.add(planBean);
                lastExecuteTime = calendar.getTimeInMillis();
            }
            for (PlanBean plan : list) {
                plan.setEndRepeatTime(lastExecuteTime);
            }
        }
        return list;
    }

    private ArrayList<PlanBean> buildOncePlan(PlanRule planRule) {
        ArrayList<PlanBean> list = new ArrayList<>();
        PlanBean oncePlan = new PlanBean();
        oncePlan.setObjectId(StringUtil.getRandomString());
        oncePlan.setPlanId(StringUtil.getRandomString());
        oncePlan.setGroupId(null);
        oncePlan.setIsAllDay(planRule.getIsAllDay());
        oncePlan.setPlanName(planRule.getPlanName());
        oncePlan.setPlanDescription(planRule.getPlanDescription());
        oncePlan.setPlanLocation(planRule.getPlanLocation());
        oncePlan.setPlanUser(planRule.getPlanUser());
        oncePlan.setStartTime(planRule.getStartTime());
        oncePlan.setAlarmTime(planRule.getAlarmTime());
        oncePlan.setRepeatMode(planRule.getRepeatType());
        oncePlan.setRepeatCount(1);
        oncePlan.setEndRepeatTime(planRule.getStartTime());
        oncePlan.setIsFinished(-1);
        oncePlan.setPlanStatus(1);
        oncePlan.setPlanType(0);
        oncePlan.setCreateTime(planRule.getCreateTime());
        oncePlan.setUpdateTime(0);
        oncePlan.setUpdateTime(0);
        list.add(oncePlan);
        return list;
    }

    /**
     * 删除 ObjectId 中对应的计划
     * 先删除数据库中对应的计划，再删除 quartz 中的提醒任务
     * 删除任务具有原子性，如果删除提醒任务失败，则数据库中的删除动作会回滚
     */
    @Transactional(rollbackFor = Exception.class)
    public Result deletePlanByObjectId(String objectId, String groupId, long updateTime) {
        try {
            int deleteResult = dao.deletePlanByObjectId(objectId, updateTime);
            if (deleteResult > 0) {
                deleteNotification(objectId, groupId);
                return new Result(true, null, null, deleteResult);
            } else {
                return new Result(true, null, null, 0);
            }
        } catch (SQLException | SchedulerException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    private void deleteNotification(String objectId, String groupId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(objectId, groupId);
        JobKey jobKey = JobKey.jobKey(objectId, groupId);
        if (scheduler.checkExists(triggerKey)) {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        }
    }


    public int getPlanCountOfDay(String userId, long dayStart, long dayEnd) {
        try {
            return dao.getPlanCount(userId, dayStart, dayEnd);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getPlanCountOfMonth(String userId, long monthStart, long monthEnd) {
        try {
            return dao.getPlanCount(userId, monthStart, monthEnd);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public Result getPlans(String userId, int year, int month, int day) {
        try {
            long startOfDay = TimeUtil.getStartOfDay(year, month, day);
            long endOfDay = TimeUtil.getEndOfDay(year, month, day);
            List<PlanBean> unFinishedPlans = dao.selectUnFinishedPlanByUserIdAndDate(userId, startOfDay, endOfDay);
            List<PlanBean> finishedPlans = dao.selectFinishedPlanByUserIdAndDate(userId, startOfDay, endOfDay);
            List<PlanBean> allPlans = new ArrayList<>();
            allPlans.addAll(unFinishedPlans);
            allPlans.addAll(finishedPlans);
            return new Result(true, null, null, allPlans);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result finishPlan(String objectId) {
        try {
            PlanBean plan = dao.getPlanByObjectId(objectId);
            //修改计划状态
            int result = dao.finishPlan(objectId, System.currentTimeMillis());
            if (result > 0) {
                //将闹钟和闹钟任务删除
                if (plan.getAlarmTime() > 0) {
                    ArrayList<PlanAlarm> alarms = dao.getAlarmList(plan.getPlanId());
                    //删除闹钟
                    dao.deleteAlarm(plan.getPlanId());
                    //删除闹钟任务
                    for (PlanAlarm alarm : alarms) {
                        deleteNotification(alarm.getObjectId(), alarm.getPlanId());
                    }
                }
                return new Result(true, null, null, null);
            } else {
                return new Result(false, "服务器出错，请稍候重试", null, null);
            }
        } catch (SQLException | SchedulerException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Result backoutFinishedPlan(String objectId) {
        try {
            int result = dao.backoutFinishedPlan(objectId, System.currentTimeMillis());
            if (result > 0) {
                PlanBean plan = dao.getPlanByObjectId(objectId);
                ArrayList<PlanAlarm> alarms = createAlarm(plan);
                addNotification(plan, alarms);
                return new Result(true, null, null, null);
            } else {
                return new Result(false, "服务器出错，请稍候重试", null, null);
            }
        } catch (SQLException | SchedulerException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result resetAlarm(String objectId, int alarmTime) {
        try {
            //先根据objectId 获取 Plan
            PlanBean planBean = dao.getPlanByObjectId(objectId);
            //如果计划有闹钟，则先将闹钟列表和闹钟任务删除
            if (planBean.getAlarmTime() > 0) {
                ArrayList<PlanAlarm> alarms = dao.getAlarmList(planBean.getPlanId());
                dao.deleteAlarm(planBean.getPlanId());
                for (PlanAlarm alarm : alarms) {
                    deleteNotification(alarm.getObjectId(), alarm.getPlanId());
                }
            }
            dao.updatePlan(objectId, alarmTime);
            planBean.setAlarmTime(alarmTime);
            ArrayList<PlanAlarm> alarms = createAlarm(planBean);
            addNotification(planBean, alarms);
            return new Result(true, null, null, null);
        } catch (SQLException | SchedulerException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result deleteSinglePlan(String objectId) {
        try {
            PlanBean planBean = dao.getPlanByObjectId(objectId);
            int count = dao.deleteSinglePlan(objectId, System.currentTimeMillis());
            if (count > 0) {
                if (planBean.getAlarmTime() >= 0) {
                    ArrayList<PlanAlarm> alarmList = dao.getAlarmList(planBean.getPlanId());
                    for (PlanAlarm alarm : alarmList) {
                        dao.deleteAlarm(alarm.getPlanId());
                        deleteNotification(alarm.getObjectId(), alarm.getPlanId());
                    }
                }
                return new Result(true, null, null, null);
            } else {
                return new Result(false, "服务器出错，请稍候重试", null, null);
            }
        } catch (SQLException | SchedulerException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result deleteGroupPlans(String groupId) {
        try {
            List<PlanBean> plans = dao.getPlansByGroupId(groupId);
            dao.deleteGroupPlans(groupId, System.currentTimeMillis());
            for (PlanBean plan : plans) {
                if (plan.getAlarmTime() >= 0) {
                    ArrayList<PlanAlarm> alarms = dao.getAlarmList(plan.getPlanId());
                    for (PlanAlarm alarm : alarms) {
                        dao.deleteAlarm(alarm.getPlanId());
                        deleteNotification(alarm.getObjectId(), alarm.getPlanId());
                    }
                }
            }
            return new Result(true, null, null, null);
        } catch (SQLException | SchedulerException e) {
            e.printStackTrace();
            return new Result(false, "服务器出错，请稍候重试", e, null);
        }
    }

    public Result getPlanOverview(String userId) {
        try {
            long monthStart = TimeUtil.getCurrentMonthStart();
            long monthEnd = TimeUtil.getCurrentMonthEnd();
            long dayStart = TimeUtil.getTodayStart();
            long dayEnd = TimeUtil.getTodayEnd();
            int dayCount = dao.getPlanCount(userId, dayStart, dayEnd);
            int monthCount = dao.getPlanCount(userId, monthStart, monthEnd);
            return new Result(true, null, null, new PlanOverview(dayCount, monthCount));
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getPlans(String userId, int year, int month) {
        int daysCount = TimeUtil.getDaysOfMonth(year, month);
        ArrayList<PlansOfMonthBean> list = new ArrayList<>(daysCount);
        for (int i = 1; i <= daysCount; i++) {
            long start = TimeUtil.getStartOfDay(year, month, i);
            long end = TimeUtil.getEndOfDay(year, month, i);
            ArrayList<PlanBean> plans = dao.getPlanByRangeTime(userId, start, end);
            PlansOfMonthBean plan = new PlansOfMonthBean();
            String monthStr;
            if (month < 10) {
                monthStr = "0" + month;
            } else {
                monthStr = month + "";
            }
            String dayStr;
            if (i < 10) {
                dayStr = "0" + i;
            } else {
                dayStr = i + "";
            }
            plan.setDate(year + "-" + monthStr + "-" + dayStr);
            plan.setPlans(plans);
            list.add(plan);
        }
        return new Result(true, null, null, list);
    }

    public Result getAllNotification(String userId) {
        try {
            List<NotificationBean> notificationBeans = new ArrayList<>();
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
                    int count = dao.checkPlan(triggerKey.getGroup(), userId);
                    if (count > 0) {
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
                        notificationBeans.add(bean);
                    }
                }
            }
            return new Result(true, null, null, notificationBeans);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return new Result(false, null, null, null);
        }
    }

    public Result getMonthPlanList(String userId, int year, int month) {
        long start = TimeUtil.getMonthStart(year, month);
        long end = TimeUtil.getMonthEnd(year, month);
        ArrayList<PlanBean> list = dao.getMonthPlanList(userId, start, end);
        return new Result(true, null, null, list);
    }
}
