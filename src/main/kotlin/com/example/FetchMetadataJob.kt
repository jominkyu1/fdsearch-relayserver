package com.example

import com.example.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.quartz.Job
import org.quartz.JobExecutionContext


class FetchMetadataJob : Job {
    override fun execute(context: JobExecutionContext?) {
        CoroutineScope(Dispatchers.IO).launch {
            async { fetchModuleOriginalData("ko") }
            async { fetchDescendantOriginalData("ko") }
            async { fetchTitleOriginalData("ko") }
            async { fetchWeaponOriginalData("ko") }
            async { fetchStatOriginalData("ko") }
            async { fetchReactorOriginalData("ko") }
            async { fetchExternalOriginalData("ko") }
        }
    }
}