package com.example

import com.example.dto.ExternalComponent
import com.example.dto.Module
import com.example.dto.RankList
import com.example.plugins.ExtractStatHelper
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
private lateinit var localRepository: LocalRepository

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init(MODE.CLOUD) // DB Init

    DatabaseFactory.createTable() // Create Tables
    DatabaseFactory.createTableEn() // Create EN Tables

    fetchFromApp = FetchFromApp()
    localRepository = LocalRepository(DatabaseFactory.dataSource)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true // NULL -> 기본값
        })
    }

    ExtractStatHelper.initModuleStatCalc()

    setupShceduler()
    configureMonitoring()


    routing {
        get("/basic_info") {
            val titlePrefixId = call.request.queryParameters["title_prefix_id"]
            val titleSuffixId = call.request.queryParameters["title_suffix_id"]
            val descendantId = call.request.queryParameters["descendant_id"]
            val descendantLevel = call.request.queryParameters["descendant_level"]?.toInt()
            val lang = call.request.queryParameters["lang"] ?: "ko"

            if(titlePrefixId == null || titleSuffixId == null || descendantId == null || descendantLevel == null){
                return@get call.respond(HttpStatusCode.Forbidden, "Parameter required")
            }

            val basicInfo = fetchFromApp.fetchCloudBasicInfo(descendantId, titlePrefixId, titleSuffixId, lang)
            basicInfo.statlist = fetchFromApp.fetchDescendantStats(descendantId, descendantLevel, lang)

            MethodCounterDto.basicInfo += 1
            call.respond(basicInfo)
        }

        post("/equipped_module"){
            val modules = call.receive<List<Module>>()
            val lang = call.request.headers["lang"] ?: "ko"

            val equippedModules = fetchFromApp.fetchEquippedModule(modules, lang)
            val calcStatList = fetchFromApp.getCalcStatTypeValue(equippedModules.map {
                it.moduleId to it.level
            })
            if(calcStatList.isNotEmpty()) equippedModules[0].calcStat = calcStatList
            // 치명타 확률, 치명타 배율 등 계산된 결과 0번 인덱스

            MethodCounterDto.eqModule += 1
            call.respond(equippedModules)
        }

        get("/weapon_entity"){
            val weaponId = call.request.queryParameters["weapon_id"]
            val weaponLevel = call.request.queryParameters["weapon_level"]?.toInt()
            if(weaponId == null || weaponLevel == null){
                return@get call.respond(HttpStatusCode.Forbidden, "Parameter required")
            }
            val lang = call.request.queryParameters["lang"] ?: "ko"

            val weaponEntity = fetchFromApp.fetchWeaponEntity(weaponId, weaponLevel, lang)

            MethodCounterDto.weaponEntity += 1
            call.respond(weaponEntity)
        }

        get("/equipped_reactor"){
            val reactorId = call.request.queryParameters["reactor_id"]
            val level = call.request.queryParameters["level"]?.toInt()
            val enchantLevel = call.request.queryParameters["enchant_level"]?.toInt()
            if(reactorId == null || level == null || enchantLevel == null){
                return@get call.respond(HttpStatusCode.Forbidden, "Parameter required")
            }
            val lang = call.request.queryParameters["lang"] ?: "ko"
            val equippedReactor = fetchFromApp.fetchEquippedReactor(reactorId, level, enchantLevel, lang)

            MethodCounterDto.eqReactor += 1
            call.respond(equippedReactor)
        }

        post("/equipped_external"){
            val externals = call.receive<List<ExternalComponent>>()
            val lang = call.request.headers["lang"] ?: "ko"

            val equippedExternal = fetchFromApp.fetchEquippedExternal(externals, lang)

            MethodCounterDto.eqExternal += 1
            call.respond(equippedExternal)
        }

        get("/rank_list"){
            val ranklist = fetchFromApp.fetchRanklist()
            call.respond(ranklist)
        }

        post("/update_usercount"){
            val deviceId = call.request.header("device-id") ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val updateKey = call.request.header("update-key") ?: return@post call.respond(HttpStatusCode.Unauthorized)
            if(updateKey != "FDSearchUPDATE") return@post call.respond(HttpStatusCode.NotAcceptable)

            val received = call.receive<RankList>()
            val username = received.username
            val rank = received.rank
            val rankExp = received.rankExp

            //1일 1조회 검증
            val hasRow = localRepository.hasDeviceIdRow(deviceId, username)
            if(hasRow) return@post call.respond(HttpStatusCode.OK)
            else localRepository.insertDeviceId(deviceId, username)

            localRepository.updateUserCount(username, rank, rankExp)
            call.respond(HttpStatusCode.OK, "OK")
        }

        get("/get_notice"){
            val notice = fetchFromApp.fetchNotice()
            call.respond(HttpStatusCode.OK, notice)
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

