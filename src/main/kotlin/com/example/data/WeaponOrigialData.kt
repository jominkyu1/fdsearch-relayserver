package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class WeaponOriginalData(
    val base_stat: List<BaseStat>,
    val firearm_atk: List<FirearmAtk>,
    val image_url: String?,
    val weapon_id: String,
    val weapon_name: String,
    val weapon_perk_ability_description: String?,
    val weapon_perk_ability_image_url: String?,
    val weapon_perk_ability_name: String?,
    val weapon_rounds_type: String?,
    val weapon_tier: String,
    val weapon_type: String?
)

@Serializable
data class BaseStat(
    val stat_id: String,
    val stat_value: Double,
)

@Serializable
data class FirearmAtk(
    val firearm: List<Firearm>,
    val level: Int
)

@Serializable
data class Firearm(
    val firearm_atk_type: String,
    val firearm_atk_value: Double
)