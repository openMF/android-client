package com.mifos.core.model.objects.template.recurring.charge

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChargeTimeType(
    val code: String? = null,
    val id: Int? = null,
    val value: String? = null,
) : Parcelable