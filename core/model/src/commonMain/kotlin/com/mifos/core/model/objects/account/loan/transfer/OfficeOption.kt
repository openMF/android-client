package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class OfficeOption(
    val id: Int,
    val name: String,
    val nameDecorated: String? = null,
) : Parcelable
