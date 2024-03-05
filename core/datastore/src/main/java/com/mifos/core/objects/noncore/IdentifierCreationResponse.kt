package com.mifos.core.objects.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 07-08-17.
 */
@Parcelize
data class IdentifierCreationResponse(
    var clientId: Int = 0,

    var officeId: Int = 0,

    var resourceId: Int = 0
) : Parcelable