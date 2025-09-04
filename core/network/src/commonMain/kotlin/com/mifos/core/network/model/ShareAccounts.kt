package com.mifos.core.network.model

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.saving.Timeline
import com.mifos.core.model.objects.template.loan.Product
import com.mifos.core.model.objects.template.loan.Status
import kotlinx.serialization.Serializable

@Serializable
data class ShareAccountResponse (
    val id : Int? = null,
    val accountNo : String? = null,
    val clientId : Int? = null,
    val clientName : String? = null,
    val productId : Int? = null,
    val productName : String? = null,
    val status: Status? = null,
    val timeline: Timeline? = null,
    val currency: Currency? = null,
)

