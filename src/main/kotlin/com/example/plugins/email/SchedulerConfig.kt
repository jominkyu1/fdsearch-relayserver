package com.example.plugins.email

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

    //매일 00:10
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("logEmailTrigger")
        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 10))
        .build()

    try{
        StdSchedulerFactory.getDefaultScheduler().apply{
            start()
            scheduleJob(job, trigger)
            logger.info("## Scheduler Service Started")
        }
    }catch(e: SchedulerException){
        logger.error("## SchedulerException. 스케쥴링 셋업 실패")
        e.printStackTrace()
    }
}