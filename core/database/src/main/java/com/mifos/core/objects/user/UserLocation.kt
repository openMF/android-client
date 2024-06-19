package com.mifos.core.objects.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 24/01/17.
 */
@Parcelize
data class UserLocation(
    var user_id: Int? = null,

    var latlng: String? = null,

    var start_time: String? = null,

    var stop_time: String? = null,

    var date: String? = null,

    var dateFormat: String? = "dd MMMM yyyy HH:mm",

    var locale: String? = "en"
) : Parcelable