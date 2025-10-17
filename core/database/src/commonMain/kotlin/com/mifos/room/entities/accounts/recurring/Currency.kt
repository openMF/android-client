package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val code: String? = null,
    val decimalPlaces: Int? = null,
    val displayLabel: String? = null,
    val displaySymbol: String? = null,
    val inMultiplesOf: Int? = null,
    val name: String? = null,
    val nameCode: String? = null,
)