package com.lixyz.lifekeeper.dao;

import com.lixyz.lifekeeper.bean.plan.PlanAlarm;
import com.lixyz.lifekeeper.bean.plan.PlanBean;
import com.lixyz.lifekeeper.bean.plan.PlanRule;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Mapper
public interface PlanDao {
    /**
     * 添加计划
     */
    @Insert("<script>" +
            "insert into Plan(ObjectId,PlanId,GroupId,IsAllDay,PlanName,PlanDescription,PlanLocation,PlanUser,StartTime,AlarmTime,RepeatMode,RepeatIndex,RepeatCount,EndRepeatTime,IsFinished,PlanStatus,PlanType,CreateTime,UpdateTime,FinishTime) " +
            " values " +
            "<foreach collection ='list' item='item' index= 'index' separator =','> " +
            "(#{item.objectId},#{item.planId},#{item.groupId},#{item.isAllDay},#{item.planName},#{item.planDescription},#{item.planLocation},#{item.planUser},#{item.startTime},#{item.alarmTime},#{item.repeatMode},#{item.repeatIndex},#{item.repeatCount},#{item.endRepeatTime},#{item.isFinished},#{item.planStatus},#{item.planType},#{item.createTime},#{item.updateTime},#{item.finishTime})" +
            "</foreach > " +
            "</script>")
    int addPlan(@Param("list") List<PlanBean> list);

    /**
     * 根据 ObjectId 删除计划
     */
    @Update("Update Plan set PlanStatus = -1 , PlanType=1 , updateTime = #{updateTime} where ObjectId = #{objectId} and planStatus = 1")
    int deletePlanByObjectId(@Param("objectId") String objectId, @Param("updateTime") long updateTime) throws SQLException;


    /**
     * 获取该用户某个时间段内的已完成有效计划
     */
    @Select("SELECT * FROM Plan WHERE planUser = #{userId} and startTime >= #{start} and startTime <= #{end} and PlanStatus = 1 and IsFinished = 1 order by startTime asc")
    List<PlanBean> selectFinishedPlanByUserIdAndDate(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    /**
     * 获取该用户某个时间段内的未完成有效计划
     */
    @Select("SELECT * FROM Plan WHERE planUser = #{userId} and startTime >= #{start} and startTime <= #{end} and PlanStatus = 1 and IsFinished = -1 order by startTime asc")
    List<PlanBean> selectUnFinishedPlanByUserIdAndDate(@Param("userId") String userId, @Param("start") long start, @Param("end") long end) throws SQLException;

    /**
     * 获取固定时间范围内的计划总数
     */
    @Select("Select count(*) from Plan where PlanUser = #{userId} and PlanStatus = 1 and StartTime >= #{dayStart} and StartTime <= #{dayEnd}")
    int getPlanCount(@Param("userId") String userId, @Param("dayStart") long dayStart, @Param("dayEnd") long dayEnd) throws SQLException;

    @Update("Update Plan set IsFinished = 1,UpdateTime = #{time},FinishTime = #{time} where objectId = #{objectId}  and PlanStatus = 1")
    int finishPlan(@Param("objectId") String objectId, @Param("time") long time) throws SQLException;

    @Select("Select * from Plan where ObjectId = #{objectId}  and PlanStatus = 1")
    PlanBean getPlanByObjectId(@Param("objectId") String objectId) throws SQLException;

    @Update("Update Plan set IsFinished = -1,UpdateTime = #{time},FinishTime = 0 where objectId = #{objectId}  and PlanStatus = 1")
    int backoutFinishedPlan(@Param("objectId") String objectId, @Param("time") long time) throws SQLException;

    @Update("Update Plan set AlarmTime = #{alarmTime},UpdateTime = ${time} where objectId = #{objectId}  and PlanStatus = 1")
    int resetAlarm(@Param("objectId") String objectId, @Param("alarmTime") int alarmTime, @Param("time") long time) throws SQLException;

    @Update("Update Plan set PlanStatus = -1, PlanType = 1,UpdateTime = #{time} where objectId = #{objectId}  and PlanStatus = 1")
    int deleteSinglePlan(@Param("objectId") String objectId, @Param("time") long time) throws SQLException;

    @Update("Update Plan set PlanStatus = -1, PlanType = 1,UpdateTime = #{time} where groupId = #{groupId} and PlanStatus = 1")
    int deleteGroupPlans(@Param("groupId") String groupId, @Param("time") long time) throws SQLException;

    @Insert("Insert into PlanRule(ObjectId,RuleId,IsAllDay,PlanName,PlanDescription,PlanLocation,PlanUser,StartTime,AlarmTime,RepeatType,EndRepeatType,EndRepeatValue,CreateTime,UpdateTime)" +
            " values " +
            "(#{rule.objectId},#{rule.ruleId},#{rule.isAllDay},#{rule.planName},#{rule.planDescription},#{rule.planLocation},#{rule.planUser},#{rule.startTime},#{rule.alarmTime},#{rule.repeatType},#{rule.endRepeatType},#{rule.endRepeatValue},#{rule.createTime},#{rule.updateTime})")
    int addPlanRule(@Param("rule") PlanRule planRule);

    @Select("Select ObjectId from Plan where groupId = #{groupId} and PlanStatus = 1")
    List<String> getObjectIdByGroupId(@Param("groupId") String groupId);

    @Select("Select GroupId from Plan where objectId = #{objectId} and PlanStatus = 1")
    String getGroupIdByObjectId(@Param("objectId") String objectId);

    @Insert("Insert into PlanAlarm(ObjectId,GroupId,PlanId,PlanName,AlarmExecuteTime,AlarmStatus,AlarmType,CreateTime,UpdateTime)" +
            " values " +
            "(#{alarm.objectId},#{alarm.groupId},#{alarm.planId},#{alarm.planName},#{alarm.alarmExecuteTime},#{alarm.alarmStatus},#{alarm.alarmType},#{alarm.createTime},#{alarm.updateTime})")
    void addPlanAlarm(@Param("alarm") PlanAlarm alarm) throws SQLException;

    @Select("Select PlanId from Plan where objectId = #{objectId} and PlanStatus = 1")
    String getPlanIdByObjectId(@Param("objectId") String objectId) throws SQLException;

    @Select("Delete from PlanAlarm where PlanId = #{planId} and AlarmStatus = 1")
    void deleteAlarm(@Param("planId") String planId);

    @Update("Update Plan set AlarmTime = #{alarmTime} where objectId = #{objectId} and PlanStatus = 1")
    void updatePlan(@Param("objectId") String objectId, @Param("alarmTime") int alarmTime);

    @Select("Select * from PlanAlarm where PlanId = #{planId} and AlarmStatus = 1")
    ArrayList<PlanAlarm> getAlarmList(@Param("planId") String planId);

    @Select("Select * from Plan where groupId = #{groupId} and PlanStatus = 1")
    List<PlanBean> getPlansByGroupId(@Param("groupId") String groupId);

    @Select("Select * from PlanAlarm where AlarmStatus = 1")
    List<PlanAlarm> getAllAlarm();

    @Select("Select * from Plan where PlanStatus = 1")
    List<PlanBean> getAllPlan();

    @Select("Select distinct PlanId from PlanAlarm where AlarmStatus = 1")
    ArrayList<String> getAlarmPlanId();

    @Select("Select * from Plan where PlanUser = #{userId} and PlanStatus = 1 and startTime >= #{start} and startTime <= #{end}")
    ArrayList<PlanBean> getPlanByRangeTime(@Param("userId") String userId, @Param("start") long start, @Param("end") long end);

    @Select("select count(*) from Plan where PlanId = #{id} and PlanUser = #{userId} and PlanStatus = 1")
    int checkPlan(@Param("id") String id, @Param("userId") String userId);


    @Select("Select * from Plan where PlanUser = #{userId} and startTime >= #{start} and startTime <= #{end} and PlanStatus = 1 order by startTime")
    ArrayList<PlanBean> getMonthPlanList(@Param("userId") String userId, @Param("start") long start, @Param("end") long end);

    @Select("Select ClientId from PushClient where UserId = #{userId}")
    String getClientId(@Param("userId") String userId);
}
