package com.example.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactorOriginalData(
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("optimized_condition_type")
    val optimizedConditionType: String?,
    @SerialName("reactor_id")
    val reactorId: String,
    @SerialName("reactor_name")
    val reactorName: String,
    @SerialName("reactor_skill_power")
    val reactorSkillPower: List<ReactorSkillPower>,
    @SerialName("reactor_tier")
    val reactorTier: String
)

@Serializable
data class ReactorSkillPower(
    @SerialName("enchant_effect")
    val enchantEffect: List<EnchantEffect>,
    @SerialName("level")
    val level: Int,
    @SerialName("skill_atk_power")
    val skillAtkPower: Double,
    @SerialName("skill_power_coefficient")
    val skillPowerCoefficient: List<SkillPowerCoefficient>,
    @SerialName("sub_skill_atk_power")
    val subSkillAtkPower: Double
)

@Serializable
data class EnchantEffect(
    @SerialName("enchant_level")
    val enchantLevel: Int,
    @SerialName("stat_type")
    val statType: String,
    @SerialName("value")
    val value: Double
)

@Serializable
data class SkillPowerCoefficient(
    @SerialName("coefficient_stat_id")
    val coefficientStatId: String,
    @SerialName("coefficient_stat_value")
    val coefficientStatValue: Double
)