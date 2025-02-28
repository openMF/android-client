/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.clients

import com.mifos.core.common.utils.Parcel
import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parceler
import com.mifos.core.common.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 15/12/16.
 */
@Parcelize
@Serializable
data class Address(
    val addressTypeId: Int? = null,
    val active: Boolean? = null,
    val street: String? = null,
    val stateProvinceId: Int? = null,
    val countryId: Int? = null,
) : Parcelable

object AddressParceler : Parceler<Address> {
    override fun Address.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(addressTypeId)
        parcel.writeBoolean(active)
        parcel.writeString(street)
        parcel.writeInt(stateProvinceId)
        parcel.writeInt(countryId)
    }

    override fun create(parcel: Parcel): Address {
        return Address(
            addressTypeId = parcel.readInt(),
            active = parcel.readBoolean(),
            street = parcel.readString(),
            stateProvinceId = parcel.readInt(),
            countryId = parcel.readInt(),
        )
    }
}
