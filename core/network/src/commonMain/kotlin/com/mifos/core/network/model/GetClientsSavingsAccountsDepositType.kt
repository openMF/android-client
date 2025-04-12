package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param code
 * @param id
 * @param `value`
 */

@Serializable
data class GetClientsSavingsAccountsDepositType(

    val code: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val value: kotlin.String? = null,

    )