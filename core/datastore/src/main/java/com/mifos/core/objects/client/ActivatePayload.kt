package com.mifos.core.objects.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 09/02/17.
 */
@Parcelize
data class ActivatePayload(
    var activationDate: String? = null,

    var dateFormat: String? = "dd MMMM YYYY",

    var locale: String? = "en",
) : Parcelable