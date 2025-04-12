package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param accountNo
 * @param currency
 * @param depositType
 * @param id
 * @param productId
 * @param productName
 * @param shortProductName
 * @param status
 */

@Serializable
data class GetClientsSavingsAccounts(

    val accountNo: kotlin.String? = null,

    val currency: GetClientsSavingsAccountsCurrency? = null,

    val depositType: GetClientsSavingsAccountsDepositType? = null,

    val id: kotlin.Long? = null,

    val productId: kotlin.Long? = null,

    val productName: kotlin.String? = null,

    val shortProductName: kotlin.String? = null,

    val status: GetClientsSavingsAccountsStatus? = null,

    )