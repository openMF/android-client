package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 21-07-2017.
 */
@Parcelize
data class AttendanceTypeOption(
    var id: Int = 0,

    var code: String? = null,

    var value: String? = null
) : Parcelable