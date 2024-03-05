/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientAddressResponse(
    var id: Int? = null,

    var clientId: Int? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    var placeAddress: String? = null,

    var placeId: String? = null,
) : Parcelable