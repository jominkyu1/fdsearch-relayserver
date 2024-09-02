package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.util.*
import org.slf4j.event.*
val CALL_START_TIME = AttributeKey<Long>("CallStartTime")
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        format {call ->
            val status = call.response.status() ?: HttpStatusCode.VersionNotSupported
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val startTime = call.attributes.getOrNull(CALL_START_TIME) ?: System.currentTimeMillis()
            val responseTime = System.currentTimeMillis() - startTime
            val ip = call.request.origin.remoteHost

            "## [RESPONSE] IP: $ip - $status [$method $uri] in ${responseTime}ms"
        }
    }
    intercept(ApplicationCallPipeline.Setup){
        call.attributes.put(CALL_START_TIME, System.currentTimeMillis())
    }
}
