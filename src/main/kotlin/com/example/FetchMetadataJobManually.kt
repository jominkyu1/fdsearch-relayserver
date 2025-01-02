package com.example

import com.example.plugins.*
import kotlinx.coroutines.*

/**
 * 테스트를 위한 수동 메타데이터 갱신
 */

class FetchMetadataJobManually {
    fun executeManually() {
        CoroutineScope(Dispatchers.IO).launch {
            logger.info("### 넥슨 메타데이터 조회 스케쥴 시작 ###")
            fetchModuleOriginalData("ko")
            fetchDescendantOriginalData("ko")
            fetchTitleOriginalData("ko")
            fetchWeaponOriginalData("ko")
            fetchStatOriginalData("ko")
            fetchReactorOriginalData("ko")
            fetchExternalOriginalData("ko")

            logger.info("### KOREAN METADATA DONE ###")

            fetchModuleOriginalData("en")
            fetchDescendantOriginalData("en")
            fetchTitleOriginalData("en")
            fetchWeaponOriginalData("en")
            fetchStatOriginalData("en")
            fetchReactorOriginalData("en")
            fetchExternalOriginalData("en")

            logger.info("### ENGLISH METADATA DONE ###")
            logger.info("### 넥슨 메타데이터 조회 스케쥴 끝 ###")

            logger.info("### 스탯 분리 스케쥴 시작 ###")
            ExtractStatHelper.initModuleStatCalc()
            logger.info("### 스탯 분리 스케쥴 끝 ###")
        }
    }
}