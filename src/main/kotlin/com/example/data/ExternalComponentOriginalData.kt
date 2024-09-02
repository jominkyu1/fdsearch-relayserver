package com.example.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalComponentOriginalData(
    @SerialName("external_component_id")
    val externalComponentId: String,
    @SerialName("external_component_name")
    val externalComponentName: String,
    @SerialName("image_url")
    val imageUrl: String,

    @SerialName("external_component_equipment_type") // 착용부위
    val externalComponentEquipmentType: String,
    @SerialName("external_component_tier")
    val externalComponentTier: String,

    @SerialName("base_stat")
    val baseStat: List<CompBaseStat>,
    @SerialName("set_option_detail") // 세트옵션
    val setOptionDetail: List<SetOptionDetail>,
)

@Serializable
data class CompBaseStat(
    @SerialName("level")
    val level: Int,
    @SerialName("stat_id")
    val statId: String,
    @SerialName("stat_value")
    val statValue: Double
)

@Serializable
data class SetOptionDetail(
    @SerialName("set_count")
    val setCount: Int,
    @SerialName("set_option")
    val setOption: String,
    @SerialName("set_option_effect")
    val setOptionEffect: String
)