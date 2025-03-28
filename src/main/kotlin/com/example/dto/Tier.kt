package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tier (
    @SerialName("tier_id")
    val tierId: String,
    @SerialName("tier_name")
    val tierName: String
)