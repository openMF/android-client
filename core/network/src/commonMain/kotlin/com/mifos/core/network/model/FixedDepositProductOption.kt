package com.mifos.core.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FixedDepositProductOption(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("withHoldTax")
    val withHoldTax: Boolean? = null,
)