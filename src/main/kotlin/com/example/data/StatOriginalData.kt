package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class StatOriginalData (
    val stat_id: String,
    val stat_name: String,
)