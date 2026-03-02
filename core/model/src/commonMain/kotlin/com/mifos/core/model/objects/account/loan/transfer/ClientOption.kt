package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ClientOption(
    val id: Int,
    val displayName: String,
    val officeId: Int? = null,
    val officeName: String? = null,
) : Parcelable
