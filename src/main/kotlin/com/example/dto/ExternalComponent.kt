package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExternalComponent (
    val externalComponentId: String,
    val externalComponentLevel: String,
)