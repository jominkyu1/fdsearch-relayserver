package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class EquippedExternal (
    val externalComponentId: String,
    val externalComponentName: String,
    val imageUrl: String,
    val externalComponentEquipmentType: String,
    val externalComponentTier: String,
    val statName: String,
    val statValue: Double,
){
    var setOptions: MutableList<EnabledSet> = mutableListOf()
}

@Serializable
data class EnabledSet(
    val enabledCount: Int,
    val setOption: String,
    val setOne: Int,
    val setOneDesc: String,
    val setTwo: Int,
    val setTwoDesc: String,
)
