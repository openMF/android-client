package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * GetClientsClientIdAccountsResponse
 *
 * @param loanAccounts
 * @param savingsAccounts
 */

@Serializable
data class GetClientsClientIdAccountsResponse(

    val loanAccounts: kotlin.collections.Set<GetClientsLoanAccounts>? = null,

    val savingsAccounts: kotlin.collections.Set<GetClientsSavingsAccounts>? = null,

    )