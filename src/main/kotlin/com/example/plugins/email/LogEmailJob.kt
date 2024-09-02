package com.example.plugins.email

import org.quartz.Job
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.Calendar


class LogEmailJob : Job {
    override fun execute(p0: JobExecutionContext?) {
        val filePath = "./logs/fdsearchlog-${getYesterday()}.log"
        EmailService.sendEmail(filePath)
    }

    private fun getYesterday(): String{
        val formatter = SimpleDateFormat("yyyyMMdd")
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.time
        return formatter.format(yesterday)
    }
}