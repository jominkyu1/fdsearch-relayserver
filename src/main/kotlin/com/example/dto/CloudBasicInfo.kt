package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class CloudBasicInfo (
    val title_prefix_name: String = "NO DATA",
    val title_suffix_name: String = "NO DATA",
    val descendant_name: String = "NO DATA",
    val descendant_image_url: String = "NO DATA",
) {
    var statlist: List<StatTypeValue> = mutableListOf()
}