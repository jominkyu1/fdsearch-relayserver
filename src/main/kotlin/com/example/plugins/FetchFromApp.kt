package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepositoryToApp
import com.example.dto.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.slf4j.LoggerFactory


class FetchFromApp{
    private val logger = LoggerFactory.getLogger("FetchFromApp")
    private val localRepository = LocalRepositoryToApp(DatabaseFactory.dataSource)
    private val json = Json {
        ignoreUnknownKeys = true // 맵핑되는 필드가 없을때 무시
        coerceInputValues = true // NULL 기본값 처리
    }

    fun fetchCloudBasicInfo(
        descendant_id: String,
        title_prefix_id: String,
        title_suffix_id: String,
    ): CloudBasicInfo{
        return localRepository.getCloudBasicInfo(descendant_id, title_prefix_id, title_suffix_id)
    }

    fun fetchEquippedModule(
        modules: List<Module>
    ): List<EquippedModule>{
        return localRepository.getEquippedModuleByIdLevel(modules)
    }

    fun fetchWeaponEntity(
        weaponId: String
    ): WeaponEntity {
        return localRepository.getWeaponEntity(weaponId)
    }

    fun fetchEquippedReactor(
        reactorId: String,
        level: Int,
        enchantLevel: Int
    ): EquippedReactor {
        return localRepository.getEquippedReactor(reactorId, level, enchantLevel)
    }
}
