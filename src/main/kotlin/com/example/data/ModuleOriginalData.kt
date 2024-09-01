package com.example.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ModuleOriginalData(
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("module_class")
    val moduleClass: String,
    @SerialName("module_id")
    val moduleId: String,
    @SerialName("module_name")
    val moduleName: String,
    @SerialName("module_socket_type")
    val moduleSocketType: String,
    @SerialName("module_stat")
    val moduleStat: List<ModuleStat>,
    @SerialName("module_tier")
    val moduleTier: String,
    @SerialName("module_type")
    val moduleType: String?,
)

@Serializable
data class ModuleStat(
    @SerialName("level")
    val level: Int,
    @SerialName("module_capacity")
    val moduleCapacity: Int,
    @SerialName("value")
    val value: String
)

