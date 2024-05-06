package com.mifos.core.objects.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */ /**
 * Created by rajan on 13/3/16.
 */
@Parcelize
data class InterestType(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable