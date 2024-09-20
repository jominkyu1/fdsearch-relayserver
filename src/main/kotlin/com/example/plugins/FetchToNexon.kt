package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepository
import com.example.LocalRepositoryEN
import com.example.data.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

val client = HttpClient(CIO) {
    install(HttpTimeout) {
        //메타데이터 용량이 크므로 타임아웃 30초
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000 // 연결 타임아웃 15초
        socketTimeoutMillis = 15_000  // 소켓 타임아웃 15초
    }
}
private val logger = LoggerFactory.getLogger("FetchToNexon")
private val localRepository = LocalRepository(DatabaseFactory.dataSource)
private val localEnRepository = LocalRepositoryEN(DatabaseFactory.dataSource)

private val json = Json {
    ignoreUnknownKeys = true // 맵핑되는 필드가 없을때 무시
    coerceInputValues = true // NULL 기본값 처리
}

private const val BASE_URL = "https://open.api.nexon.com"
private fun fetchLog(name: String, size: Int, language_code: String){
    logger.info("Fetched $name from Server [SIZE] : $size [Language] : $language_code")
}

suspend fun fetchDescendantOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/descendant.json").bodyAsText()
    val decodedList: List<DescendantOriginalData> = json.decodeFromString(response)
    fetchLog("descendant.json", decodedList.size, language_code)

    when(language_code){
        "ko" -> { localRepository.insertDescendantsMetadata(decodedList) }
        "en" -> { localEnRepository.insertDescendantsMetadata(decodedList) }
    }
}

suspend fun fetchTitleOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/title.json").bodyAsText()
    val decodedList: List<TitleOriginalData> = json.decodeFromString(response)
    fetchLog("title.json", decodedList.size, language_code)

    when(language_code){
        "ko" -> { localRepository.insertTitleMetadata(decodedList) }
        "en" -> { localEnRepository.insertTitleMetadata(decodedList) }
    }
}

suspend fun fetchWeaponOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/weapon.json").bodyAsText()
    val decodedList: List<WeaponOriginalData> = json.decodeFromString(response)
    fetchLog("weapon.json", decodedList.size, language_code)

    when(language_code){
        "ko" -> { localRepository.insertWeaponMetadata(decodedList) }
        "en" -> { localEnRepository.insertWeaponMetadata(decodedList) }
    }
}

suspend fun fetchStatOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/stat.json").bodyAsText()
    val decodedList: List<StatOriginalData> = json.decodeFromString(response)
    fetchLog("stat.json", decodedList.size, language_code)

    when(language_code){
        "ko" -> { localRepository.insertStatMetaData(decodedList) }
        "en" -> { localEnRepository.insertStatMetaData(decodedList) }
    }
}

suspend fun fetchModuleOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/module.json").bodyAsText()
    val decodedList: List<ModuleOriginalData> = json.decodeFromString(response)
    fetchLog("module.json", decodedList.size, language_code)

    when(language_code){
        "ko" -> { localRepository.insertModuleMetaData(decodedList) }
        "en" -> { localEnRepository.insertModuleMetaData(decodedList) }
    }
}

suspend fun fetchReactorOriginalData(language_code: String){
    //리액터 메타데이터는 용량이 크므로 3번 재시도
    repeat(3) {
        try {
            val response = client.get("$BASE_URL/static/tfd/meta/$language_code/reactor.json").bodyAsText()
            val decodedList: List<ReactorOriginalData> = json.decodeFromString(response)
            fetchLog("reactor.json", decodedList.size, language_code)

            when(language_code){
                "ko" -> { localRepository.insertReactorMetaData(decodedList) }
                "en" -> { localEnRepository.insertReactorMetaData(decodedList) }
            }

            return
        }catch(e: HttpRequestTimeoutException) {
            logger.error("Reactor 메타데이터 조회 실패. 재시도.. : ${it + 1}")
        }
    }

    logger.error("Reactor 메타데이터 최종 조회 실패")
}

suspend fun fetchExternalOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/external-component.json").bodyAsText()
    val decodedList: List<ExternalComponentOriginalData> = json.decodeFromString(response)
    fetchLog("external.component.json", decodedList.size, language_code)

    when(language_code){
        "ko" -> { localRepository.insertExternalCompMetaData(decodedList) }
        "en" -> { localEnRepository.insertExternalCompMetaData(decodedList) }
    }
}