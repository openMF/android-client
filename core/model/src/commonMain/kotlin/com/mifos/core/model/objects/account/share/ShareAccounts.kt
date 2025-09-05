package com.mifos.core.model.objects.account.share

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.saving.Timeline

data class ShareAccounts (
    val id : Int? = null,
    val accountNo : String? = null,
    val totalApprovedShares : Int? = null,
    val totalPendingForApprovalShares : Int? = null,
    val shortProductName: String? = null,
    val clientId : Int? = null,
    val clientName : String? = null,
    val productId : Int? = null,
    val productName : String? = null,
    val status: ShareAccountsStatus? = null,
)



