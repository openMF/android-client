package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class CenterDetail(
    var staffId: Int = 0,

    var staffName: String? = null,

    var meetingFallCenters: List<MeetingFallCalendar>? = ArrayList()
) : Parcelable