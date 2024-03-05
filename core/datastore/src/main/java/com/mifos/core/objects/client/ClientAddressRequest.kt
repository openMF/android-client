/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientAddressRequest(
    var placeId: String? = null,

    var placeAddress: String? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    // Defaults
    var dateFormat: String? = "dd MMMM YYYY",

    var locale: String? = "en"
) : Parcelable