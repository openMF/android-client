package com.mifos.core.model.objects.account.share

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.saving.Timeline
import com.mifos.core.model.objects.template.loan.Product
import com.mifos.core.model.objects.template.loan.Status
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
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
    val timeline: Timeline? = null,
    val currency: Currency? = null,
): Parcelable



