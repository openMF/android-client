package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param active
 * @param closed
 * @param closedObligationsMet
 * @param closedRescheduled
 * @param closedWrittenOff
 * @param code
 * @param description
 * @param id
 * @param overpaid
 * @param pendingApproval
 * @param waitingForDisbursal
 */

@Serializable
data class GetClientsLoanAccountsStatus(

    val active: kotlin.Boolean? = null,

    val closed: kotlin.Boolean? = null,

    val closedObligationsMet: kotlin.Boolean? = null,

    val closedRescheduled: kotlin.Boolean? = null,

    val closedWrittenOff: kotlin.Boolean? = null,

    val code: kotlin.String? = null,

    val description: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val overpaid: kotlin.Boolean? = null,

    val pendingApproval: kotlin.Boolean? = null,

    val waitingForDisbursal: kotlin.Boolean? = null,

    )