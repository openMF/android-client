package com.mifos.core.model.objects.template.recurring

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class Timeline(
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val submittedByUsername: String? = null,
    val submittedOnDate: List<Int>? = null,
): Parcelable