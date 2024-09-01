package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepository
import com.example.data.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

val client = HttpClient(CIO)
private val logger = LoggerFactory.getLogger("FetchToNexon")
private val localRepository = LocalRepository(DatabaseFactory.dataSource)
private val json = Json {
    ignoreUnknownKeys = true // 맵핑되는 필드가 없을때 무시
    coerceInputValues = true // NULL 기본값 처리
}

private const val BASE_URL = "https://open.api.nexon.com"
private fun fetchLog(name: String, size: Int){
    logger.info("Fetched $name from Server [SIZE] : $size")
}

suspend fun Application.fetchDescendantOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/descendant.json").bodyAsText()
    val decodedList: List<DescendantOriginalData> = json.decodeFromString(response)
    fetchLog("descendant.json", decodedList.size)

    localRepository.insertDescendantsMetadata(decodedList)
}

suspend fun Application.fetchTitleOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/title.json").bodyAsText()
    val decodedList: List<TitleOriginalData> = json.decodeFromString(response)
    fetchLog("title.json", decodedList.size)

    localRepository.insertTitleMetadata(decodedList)
}

suspend fun Application.fetchWeaponOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/weapon.json").bodyAsText()
    val decodedList: List<WeaponOriginalData> = json.decodeFromString(response)
    fetchLog("weapon.json", decodedList.size)

    localRepository.insertWeaponMetadata(decodedList)
}

suspend fun Application.fetchStatOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/stat.json").bodyAsText()
    val decodedList: List<StatOriginalData> = json.decodeFromString(response)
    fetchLog("stat.json", decodedList.size)

    localRepository.insertStatMetaData(decodedList)
}

suspend fun Application.fetchModuleOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/module.json").bodyAsText()
    val decodedList: List<ModuleOriginalData> = json.decodeFromString(response)
    fetchLog("module.json", decodedList.size)

    localRepository.insertModuleMetaData(decodedList)
}

suspend fun Application.fetchReactorOriginalData(language_code: String){
    val response = client.get("$BASE_URL/static/tfd/meta/$language_code/reactor.json").bodyAsText()
    val decodedList: List<ReactorOriginalData> = json.decodeFromString(response)
    fetchLog("reactor.json", decodedList.size)

    localRepository.insertReactorMetaData(decodedList)
}