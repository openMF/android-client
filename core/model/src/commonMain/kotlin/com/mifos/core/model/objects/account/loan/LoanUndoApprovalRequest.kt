package com.mifos.core.model.objects.account.loan

import kotlinx.serialization.Serializable

@Serializable
data class LoanUndoApprovalRequest(
    val note: String? = null
)
