package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val active: Boolean? = null,
    val approved: Boolean? = null,
    val closed: Boolean? = null,
    val code: String? = null,
    val id: Int? = null,
    val matured: Boolean? = null,
    val prematureClosed: Boolean? = null,
    val rejected: Boolean? = null,
    val submittedAndPendingApproval: Boolean? = null,
    val transferInProgress: Boolean? = null,
    val transferOnHold: Boolean? = null,
    val value: String? = null,
    val withdrawnByApplicant: Boolean? = null,
)