package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Module (
    val module_id: String,
    val module_enchant_level: Int,
)