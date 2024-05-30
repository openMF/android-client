/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.group

import android.os.Parcelable
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.client.Status
import com.mifos.core.objects.collectionsheet.CollectionMeetingCalendar
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 28/06/14.
 */
@Parcelize
data class CenterWithAssociations(
    var id: Int? = null,

    var accountNo: String? = null,

    var name: String? = null,

    var externalId: String? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var staffId: Int? = null,

    var staffName: String? = null,

    var hierarchy: String? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var activationDate: List<Int> = ArrayList(),

    var timeline: Timeline? = null,

    var groupMembers: List<Group> = ArrayList(),

    var collectionMeetingCalendar: CollectionMeetingCalendar = CollectionMeetingCalendar()
) : Parcelable