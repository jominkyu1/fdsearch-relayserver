package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankList(
     val rank: Int,
     val username: String,
     @SerialName("rank_exp")
     val rankExp: Int,
     val count: Int,
)
