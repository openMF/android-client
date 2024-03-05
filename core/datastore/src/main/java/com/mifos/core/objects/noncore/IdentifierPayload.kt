package com.mifos.core.objects.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 01/10/16.
 */
@Parcelize
data class IdentifierPayload(
    var documentTypeId: Int? = null,

    var documentKey: String? = null,

    var status: String? = null,

    var description: String? = null
) : Parcelable