package com.example.plugins.email

import com.example.DatabaseFactory
import com.example.LocalRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory


class LogRequestCount : Job {
    private val logger = LoggerFactory.getLogger("LogRequestCount")
    private val localRepository = LocalRepository(DatabaseFactory.dataSource)

    override fun execute(p0: JobExecutionContext?) {
        localRepository.updateQueryCount()
        clearCount()
        logger.info("RequestCount 테이블 업데이트 성공 [Every 2hour]")
    }

    private fun clearCount(){
       MethodCounterDto.apply {
           eqReactor = 0
           eqExternal = 0
           eqModule = 0
           basicInfo = 0
           weaponEntity = 0
           denied = 0
       }
    }
}