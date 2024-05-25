package com.mifos.core.objects.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 15/12/16.
 */
@Parcelize
data class Address(
    var addressTypeId: Int? = null,

    var active: Boolean? = null,

    var street: String? = null,

    var stateProvinceId: Int? = null,

    var countryId: Int? = null
) : Parcelable