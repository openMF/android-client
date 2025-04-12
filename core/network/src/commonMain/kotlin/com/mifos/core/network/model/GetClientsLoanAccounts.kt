package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param accountNo
 * @param externalId
 * @param id
 * @param loanCycle
 * @param loanType
 * @param productId
 * @param productName
 * @param status
 */

@Serializable
data class GetClientsLoanAccounts(

    val accountNo: kotlin.String? = null,

    val externalId: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val loanCycle: kotlin.Int? = null,

    val loanType: GetClientsLoanAccountsType? = null,

    val productId: kotlin.Long? = null,

    val productName: kotlin.String? = null,

    val status: GetClientsLoanAccountsStatus? = null,

    )