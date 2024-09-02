package com.example

import com.example.dto.ExternalComponent
import com.example.dto.Module
import com.example.plugins.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("KtorServerLogger")
private lateinit var fetchFromApp: FetchFromApp

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init() // DB Init
    DatabaseFactory.createTable() // Create Tables
    fetchFromApp = FetchFromApp()
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true // NULL -> 기본값
        })
    }
    CoroutineScope(Dispatchers.IO).launch{
        fetchModuleOriginalData("ko")
        fetchDescendantOriginalData("ko")
        fetchTitleOriginalData("ko")
        fetchWeaponOriginalData("ko")
        fetchStatOriginalData("ko")
        fetchReactorOriginalData("ko")
        fetchExternalOriginalData("ko")
    }

    configureMonitoring()

    routing {
        get("/hi"){
            call.respond("yes Hi")
        }

        get("/basic_info") {
            val titlePrefixId = call.request.queryParameters["title_prefix_id"]!!
            val titleSuffixId = call.request.queryParameters["title_suffix_id"]!!
            val descendantId = call.request.queryParameters["descendant_id"]!!

            val basicInfo = fetchFromApp.fetchCloudBasicInfo(descendantId, titlePrefixId, titleSuffixId)
            call.respond(basicInfo)
        }

        post("/equipped_module"){
            val modules = call.receive<List<Module>>()

            val equippedModules = fetchFromApp.fetchEquippedModule(modules)
            call.respond(equippedModules)
        }

        get("/weapon_entity"){
            val weaponId = call.request.queryParameters["weapon_id"]!!

            val weaponEntity = fetchFromApp.fetchWeaponEntity(weaponId)
            call.respond(weaponEntity)
        }

        get("/equipped_reactor"){
            val reactorId = call.request.queryParameters["reactor_id"]!!
            val level = call.request.queryParameters["level"]?.toInt()!!
            val enchantLevel = call.request.queryParameters["enchant_level"]?.toInt()!!

            val equippedReactor = fetchFromApp.fetchEquippedReactor(reactorId, level, enchantLevel)

            call.respond(equippedReactor)
        }

        post("/equipped_external"){
            val externals = call.receive<List<ExternalComponent>>()
            val equippedExternal = fetchFromApp.fetchEquippedExternal(externals)

            call.respond(equippedExternal)
        }
    }
}

