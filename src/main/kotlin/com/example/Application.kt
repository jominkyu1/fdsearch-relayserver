package com.example

import com.example.dto.ExternalComponent
import com.example.dto.Module
import com.example.plugins.FetchFromApp
import com.example.plugins.configureMonitoring
import com.example.plugins.email.MethodCounterDto
import com.example.plugins.email.setupShceduler
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("KtorServerLogger")
private lateinit var fetchFromApp: FetchFromApp

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init(MODE.CLOUD) // DB Init

    DatabaseFactory.createTable() // Create Tables
    fetchFromApp = FetchFromApp()
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true // NULL -> 기본값
        })
    }
    setupShceduler()
    configureMonitoring()

    routing {
        get("/basic_info") {
            val titlePrefixId = call.request.queryParameters["title_prefix_id"]!!
            val titleSuffixId = call.request.queryParameters["title_suffix_id"]!!
            val descendantId = call.request.queryParameters["descendant_id"]!!

            val basicInfo = fetchFromApp.fetchCloudBasicInfo(descendantId, titlePrefixId, titleSuffixId)
            MethodCounterDto.basicInfo += 1
            call.respond(basicInfo)
        }

        post("/equipped_module"){
            val modules = call.receive<List<Module>>()

            val equippedModules = fetchFromApp.fetchEquippedModule(modules)

            MethodCounterDto.eqModule += 1
            call.respond(equippedModules)
        }

        get("/weapon_entity"){
            val weaponId = call.request.queryParameters["weapon_id"]!!
            val weaponLevel = call.request.queryParameters["weapon_level"]!!.toInt()

            val weaponEntity = fetchFromApp.fetchWeaponEntity(weaponId, weaponLevel)

            MethodCounterDto.weaponEntity += 1
            call.respond(weaponEntity)
        }

        get("/equipped_reactor"){
            val reactorId = call.request.queryParameters["reactor_id"]!!
            val level = call.request.queryParameters["level"]?.toInt()!!
            val enchantLevel = call.request.queryParameters["enchant_level"]?.toInt()!!

            val equippedReactor = fetchFromApp.fetchEquippedReactor(reactorId, level, enchantLevel)

            MethodCounterDto.eqReactor += 1
            call.respond(equippedReactor)
        }

        post("/equipped_external"){
            val externals = call.receive<List<ExternalComponent>>()
            val equippedExternal = fetchFromApp.fetchEquippedExternal(externals)

            MethodCounterDto.eqExternal += 1
            call.respond(equippedExternal)
        }

        // routing 제외 DENY
        route("{...}") {
            handle {
                MethodCounterDto.denied += 1
                call.respond(HttpStatusCode.Forbidden, "Access denied")
            }
        }
    }
}

