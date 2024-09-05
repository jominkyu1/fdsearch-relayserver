package com.example.plugins

import com.example.DatabaseFactory
import com.example.LocalRepositoryToApp
import com.example.dto.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory


class FetchFromApp{
    private val logger = LoggerFactory.getLogger("FetchFromApp")
    private val localRepository = LocalRepositoryToApp(DatabaseFactory.dataSource)

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
        weaponId: String,
        weaponLevel: Int
    ): WeaponEntity {
        return localRepository.getWeaponEntity(weaponId, weaponLevel)
    }

    fun fetchEquippedReactor(
        reactorId: String,
        level: Int,
        enchantLevel: Int
    ): EquippedReactor {
        return localRepository.getEquippedReactor(reactorId, level, enchantLevel)
    }

    fun fetchEquippedExternal(
        externals: List<ExternalComponent>
    ): List<EquippedExternal>{
        return localRepository.getEquippedExternal(externals)
    }
}
