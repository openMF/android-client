package com.mifos.core.objects.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 13-08-17.
 */
@Parcelize
data class ChargeCreationResponse(
    var clientId: Int = 0,

    var officeId: Int = 0,

    var resourceId: Int = 0
) : Parcelable