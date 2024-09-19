package com.example

import com.example.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FetchMetadataEnJob {
    fun execute() {
        CoroutineScope(Dispatchers.IO).launch {
            async { fetchModuleOriginalData("en") }
            async { fetchDescendantOriginalData("en") }
            async { fetchTitleOriginalData("en") }
            async { fetchWeaponOriginalData("en") }
            async { fetchStatOriginalData("en") }
            async { fetchReactorOriginalData("en") }
            async { fetchExternalOriginalData("en") }
        }
    }
}