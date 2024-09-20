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

    val statValue: Int = -1,
    val statName: String = "NOT FOUND",

    val statValue2: Int = -1,
    val statName2: String = "NOT FOUND",

    val statValue3: Int = -1,
    val statName3: String = "NOT FOUND",
)