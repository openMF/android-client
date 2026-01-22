package com.mifos.core.model.objects.template.recurring.approval

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositApprovall(
    var locale: String = "en",

    var dateFormat: String = "dd MMMM yyyy",

    var approvedOnDate: String? = null,

    var note: String? = null,
)

