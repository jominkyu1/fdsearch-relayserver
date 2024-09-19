package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepositoryToApp
import com.example.dto.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory


class FetchFromApp{
    private val logger = LoggerFactory.getLogger("FetchFromApp")
    private val localRepository = LocalRepositoryToApp(DatabaseFactory.dataSource)

    fun fetchRanklist()
    : List<RankList>{
        return localRepository.getRankList()
    }

    fun fetchCloudBasicInfo(
        descendant_id: String,
        title_prefix_id: String,
        title_suffix_id: String,
        lang: String = "ko",
    ): CloudBasicInfo{
        return localRepository.getCloudBasicInfo(descendant_id, title_prefix_id, title_suffix_id, lang)
    }

    fun fetchEquippedModule(
        modules: List<Module>,
        lang: String = "ko"
    ): List<EquippedModule>{
        return localRepository.getEquippedModuleByIdLevel(modules, lang)
    }

    fun fetchWeaponEntity(
        weaponId: String,
        weaponLevel: Int,
        lang: String = "ko"
    ): WeaponEntity {
        return localRepository.getWeaponEntity(weaponId, weaponLevel, lang)
    }

    fun fetchEquippedReactor(
        reactorId: String,
        level: Int,
        enchantLevel: Int,
        lang: String = "ko"
    ): EquippedReactor {
        return localRepository.getEquippedReactor(reactorId, level, enchantLevel, lang)
    }

    fun fetchEquippedExternal(
        externals: List<ExternalComponent>,
        lang: String = "ko"
    ): List<EquippedExternal>{
        return localRepository.getEquippedExternal(externals, lang)
    }

    fun fetchNotice(): Notice {
        return localRepository.getNotice()
    }
}
