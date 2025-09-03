package com.mifos.core.network.model

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PostClientAddressRequest(
    val addressLine1: String = "",

    val addressLine2: String = "",

    val addressLine3: String = "",

    val city: String = "",

    val stateProvinceId: Int = -1,

    val countryId: Int = -1,

    val postalCode: String = "",
) : Parcelable
