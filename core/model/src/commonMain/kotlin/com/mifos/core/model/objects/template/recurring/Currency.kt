package com.mifos.core.model.objects.template.recurring

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Currency(
    val code: String? = null,
    val decimalPlaces: Int? = null,
    val displayLabel: String? = null,
    val displaySymbol: String? = null,
    val name: String? = null,
    val nameCode: String? = null,
) : Parcelable