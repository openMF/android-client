package com.mifos.core.model.objects.template.recurring

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ClientTypeOption(
    val active: Boolean? = null,
    val description: String? = null,
    val id: Int? = null,
    val mandatory: Boolean? = null,
    val name: String? = null,
    val position: Int? = null,
) : Parcelable