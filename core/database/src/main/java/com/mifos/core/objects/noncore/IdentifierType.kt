package com.mifos.core.objects.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 01/10/16.
 */
@Parcelize
data class IdentifierType(
    var id: Int? = null,

    var name: String? = null,

    var position: Int? = null
) : Parcelable