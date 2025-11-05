package com.mifos.core.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FixedDepositProductOption(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("shortName")
    val shortName: String,

    @SerialName("totalShares")
    val totalShares: Int,
)