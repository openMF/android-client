/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 16/07/14.
 */
@Parcelize
data class CollectionMeetingCalendar(
    var id: Int = 0,

    var calendarInstanceId: Int? = null,

    var entityId: Int? = null,

    var entityType: EntityType? = null,

    var title: String? = null,

    var startDate: List<Int> = ArrayList(),

    var duration: Int? = null,

    var type: EntityType? = null,

    var repeating: Boolean? = null,

    var recurrence: String? = null,

    var frequency: EntityType? = null,

    var interval: Int? = null,

    var repeatsOnDay: EntityType? = null,

    var firstReminder: Int? = null,

    var secondReminder: Int? = null,

    var recurringDates: List<List<Int>> = ArrayList(),

    var nextTenRecurringDates: List<List<Int>> = ArrayList(),

    var humanReadable: String? = null,

    var recentEligibleMeetingDate: List<Int> = ArrayList(),

    var createdDate: List<Int> = ArrayList(),

    var lastUpdatedDate: List<Int> = ArrayList(),

    var createdByUserId: Int? = null,

    var createdByUsername: String? = null,

    var lastUpdatedByUserId: Int? = null,

    var lastUpdatedByUsername: String? = null,
) : Parcelable