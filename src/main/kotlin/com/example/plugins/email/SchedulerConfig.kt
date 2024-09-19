package com.example.plugins.email

import com.example.FetchMetadataJob
import io.ktor.server.application.*
import org.quartz.CronExpression
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.SimpleScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("EmailScheduler")
fun setupShceduler(){
    val job: JobDetail = JobBuilder.newJob(LogEmailJob::class.java)
        .withIdentity("logEmailJob")
        .build()
    val requestCountJob: JobDetail = JobBuilder.newJob(LogRequestCount::class.java)
        .withIdentity("LogRequestCountHourlyJob")
        .build()
    val fetchMetadataJob: JobDetail = JobBuilder.newJob(FetchMetadataJob::class.java)
        .withIdentity("LogFetchMetadataJob")
        .build()
    val clearCountTimeJob: JobDetail = JobBuilder.newJob(ClearCountTimeJob::class.java)
        .withIdentity("ClearCountTimeJob")
        .build()

    //매일 00:05
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("logEmailTrigger")
        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 5))
        .build()
    //매일 00:00
    val dailyTrigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("ClearCountTimeJobTrigger")
        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 5))
        .build()

    //매일 1시간 단위
    val hourlyTrigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity(("logRequestCountHourlyTrigger"))
        .withSchedule(SimpleScheduleBuilder.repeatHourlyForever())
        .build()
    
    // 일주일 단위 (매주 목요일 13:00)
    // CRON 초 분 시 일 월 요일 연도
    val weeklyTrigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("logWeeklyTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 13 ? * 5 *"))
        .build()

    // 일주일 단위 (매주 목요일 13:00)
    // CRON 초 분 시 일 월 요일 연도
    val weeklyTrigger2: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("logWeeklyTrigger2")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 13 ? * 5 *"))
        .build()

    //1분 단위 [테스트용]
    val minuteTrigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity(("repeatMinuteForeverTrigger"))
        .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever())
        .build()
    try{
        StdSchedulerFactory.getDefaultScheduler().apply{
            start()
            scheduleJob(job, trigger) // Send email
            scheduleJob(requestCountJob, hourlyTrigger) // endpoint count
            scheduleJob(fetchMetadataJob, weeklyTrigger) // fetch metadata
            scheduleJob(clearCountTimeJob, dailyTrigger) // post user count
            logger.info("## Scheduler Service Started")
        }
    }catch(e: SchedulerException){
        logger.error("## SchedulerException. 스케쥴링 셋업 실패")
        e.printStackTrace()
    }
}