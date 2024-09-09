package com.example.plugins.email

import com.example.DatabaseFactory
import com.example.LocalRepository
import org.quartz.Job
import org.quartz.JobExecutionContext

class ClearCountTimeJob: Job {
    private val localRepository = LocalRepository(DatabaseFactory.dataSource)

    override fun execute(context: JobExecutionContext?) {
        localRepository.clearCountTime()
    }
}