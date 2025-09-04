package com.mifos.core.model.objects.account.share

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ShareAccountsStatus(
    val id: Int? = null,

    val code: String? = null,

    val value: String? = null,

    val submittedAndPendingApproval: Boolean? = null,

    val approved: Boolean? = null,

    val rejected: Boolean? = null,
    
    val active: Boolean? = null,

    val closed: Boolean? = null,
): Parcelable