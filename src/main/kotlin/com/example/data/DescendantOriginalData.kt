package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class DescendantOriginalData(
    val descendant_id: String,
    val descendant_name: String,
    val descendant_image_url: String,
    val descendant_stat: List<DescendantOriginalStatEntity>,
    val descendant_skill: List<DescendantOriginalSkillEntity>,
)

@Serializable
data class DescendantOriginalStatEntity (
    val level: Int,
    val stat_detail: List<DescendantOriginalStatDetailEntity>
)

@Serializable
data class DescendantOriginalSkillEntity(
    val skill_type: String,
    val skill_name: String,
    val element_type: String,
    val arche_type: String?,
    val skill_image_url: String,
    val skill_description: String,
)

@Serializable
data class DescendantOriginalStatDetailEntity (
    val stat_type: String,
    val stat_value: Double,
)