package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatTypeValue(
    @SerialName("stat_type")
    val statType: String,
    @SerialName("stat_value")
    val statValue: String,
)
