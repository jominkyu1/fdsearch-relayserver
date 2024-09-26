package com.example

import com.example.plugins.*
import kotlinx.coroutines.*
import org.quartz.Job
import org.quartz.JobExecutionContext


class FetchMetadataJob : Job {
    override fun execute(context: JobExecutionContext?) {
        CoroutineScope(Dispatchers.IO).launch {
            logger.info("### 넥슨 메타데이터 조회 스케쥴 시작 ###")
            val koTasks = listOf(
                async { fetchModuleOriginalData("ko") },
                async { fetchDescendantOriginalData("ko") },
                async { fetchTitleOriginalData("ko") },
                async { fetchWeaponOriginalData("ko") },
                async { fetchStatOriginalData("ko") },
                async { fetchReactorOriginalData("ko") },
                async { fetchExternalOriginalData("ko") }
            )

            koTasks.awaitAll()
            logger.info("### KOREAN METADATA DONE ###")

            val enTasks = listOf(
                async { fetchModuleOriginalData("en") },
                async { fetchDescendantOriginalData("en") },
                async { fetchTitleOriginalData("en") },
                async { fetchWeaponOriginalData("en") },
                async { fetchStatOriginalData("en") },
                async { fetchReactorOriginalData("en") },
                async { fetchExternalOriginalData("en") }
            )

            enTasks.awaitAll()
            logger.info("### ENGLISH METADATA DONE ###")
            logger.info("### 넥슨 메타데이터 조회 스케쥴 끝 ###")

            logger.info("### 스탯 분리 스케쥴 시작 ###")
            ExtractStatHelper.initModuleStatCalc()
            logger.info("### 스탯 분리 스케쥴 끝 ###")
        }
    }
}