package com.mifos.core.model.objects.template.recurring.period

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ProductOption(
    val id: Int? = null,
    val name: String? = null,
    val withHoldTax: Boolean? = null,
) : Parcelable