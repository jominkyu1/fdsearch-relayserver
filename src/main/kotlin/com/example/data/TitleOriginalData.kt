package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class TitleOriginalData (
    val title_id: String,
    val title_name: String
)