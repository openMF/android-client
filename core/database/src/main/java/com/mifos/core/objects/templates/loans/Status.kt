package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Status(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable