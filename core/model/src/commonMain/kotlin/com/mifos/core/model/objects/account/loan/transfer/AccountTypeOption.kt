package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AccountTypeOption(
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
) : Parcelable
