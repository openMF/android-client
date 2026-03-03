package com.mifos.core.model.objects.account.loan.transfer

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class OfficeOption(
    val id: Int? = null,
    val name: String? = null,
    val nameDecorated: String? = null,
) : Parcelable
