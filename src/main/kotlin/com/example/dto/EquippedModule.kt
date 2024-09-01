package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class EquippedModule(
    val moduleId: String,
    // Default Info (from Module)
    val moduleName: String,
    val moduleClass: String, // 계승자, 고위력탄, 일반탄, 충격탄, 특수탄
    val moduleSocketType: String,
    val moduleType: String?, // 공격, 전투, 정신력, 정확도, 제어...etc
    val moduleTier: String,
    val imageUrl: String,

    //Detail Info (from ModuleStat by moduleId)
    val level: Int,
    val moduleCapacity: Int,
    val value: String, // 치명타 저항력 +1~15% ..etc
)