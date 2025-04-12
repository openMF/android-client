package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param code
 * @param decimalPlaces
 * @param displayLabel
 * @param displaySymbol
 * @param name
 * @param nameCode
 */

@Serializable
data class GetClientsSavingsAccountsCurrency(

    val code: kotlin.String? = null,

    val decimalPlaces: kotlin.Int? = null,

    val displayLabel: kotlin.String? = null,

    val displaySymbol: kotlin.String? = null,

    val name: kotlin.String? = null,

    val nameCode: kotlin.String? = null,

    )