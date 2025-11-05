package com.mifos.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class FixedDepositTemplate(
    @SerialName("clientId")
    val clientId: Int,

    @SerialName(value = "clientName")
    val clientName: String,

    @SerialName("productOptions")
    val productOptions: List<FixedDepositProductOption> = emptyList(),
)