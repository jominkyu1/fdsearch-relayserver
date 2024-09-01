package com.example.dto

import com.example.data.SkillPowerCoefficient
import kotlinx.serialization.Serializable

@Serializable
data class EquippedReactor (
    var reactorId: String = "",
    var reactorSlotId: String = "",
    var reactorName: String = "",
    var reactorTier: String = "",
    var level: Int = 0,

    var reactorEnchantLevel: Int = 0,
    var statTypeByLevelAndEnchantLevel: String = "",
    var statValueByLevelAndEnchantLevel: Double = 0.0,

    var skillAtkPower: String = "", //스킬위력
    var subSkillAtkPower: String = "", //보조공격위력
    var optimizedConditionType: String? = "", // 최적화 조건
    var imageUrl: String = "",
) {
    var coefficientList: MutableList<ReactorCoefficient> = mutableListOf()
}

//속성추뎀
@Serializable
data class ReactorCoefficient (
    val coefficientStatId: String,
    val coefficientStatValue: Double
        )