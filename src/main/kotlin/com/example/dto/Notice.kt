package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class Notice(
    val value: String="",
    val date: String="",
)
