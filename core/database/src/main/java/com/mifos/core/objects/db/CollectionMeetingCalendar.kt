/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollectionMeetingCalendar(
    var calendarInstanceId: Int = 0,

    var calendarId: Long = 0,

    var entityId: Int = 0,

    var entityType: EntityType? = null,

    var title: String? = null,

    var description: String? = null,

    var location: String? = null,

    var meetingCalendarDate: MeetingDate? = null,

    var isRepeating: Boolean = false,

    var recurrence: String? = null,

    var startDate: List<Int> = ArrayList()
) : Parcelable