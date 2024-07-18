package com.mifos.core.objects.checkerinboxandtasks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RescheduleReasonCodeValue(
    var id: Int,
    var name: String,
    var active: Boolean,
    var mandatory: Boolean
) : Parcelable