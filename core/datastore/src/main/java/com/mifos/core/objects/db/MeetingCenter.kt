/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetingCenter(
    var name: String? = null,

    var externalId: String? = null,

    var officeId: Int = 0,

    var staffId: Int = 0,

    var staffName: String? = null,

    var status: Status? = null,

    var isActive: Boolean = false,

    var meetingDate: MeetingDate? = null,

    var collectionMeetingCalendar: CollectionMeetingCalendar? = null,

    var isSynced: Int = 0,

    var centerId: Long = 0,

    var activationDate: List<Int>? = null,
) : Parcelable