package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeaponEntity (
    val weaponId: String = "NOT FOUND",
    val imageUrl: String = "NOT FOUND",
    val weaponName: String = "NOT FOUND",
    val weaponPerkAbilityDescription: String? = "NOT FOUND",
    val weaponPerkAbilityImageUrl: String? = "NOT FOUND",
    val weaponPerkAbilityName: String? = "NOT FOUND",
    val weaponRoundsType: String = "NOT FOUND",
    val weaponTier: String = "NOT FOUND",
    val weaponType: String = "NOT FOUND",
    val firearmAtkValue: Int = -1,
    val statName: String = "NOT FOUND"
)