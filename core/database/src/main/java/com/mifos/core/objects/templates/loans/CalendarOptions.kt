package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class CalendarOptions(
    var id: Int? = null,

    var calendarInstanceId: Int? = null,

    var entityId: Int? = null,

    var entityType: EntityType? = null,

    var title: String? = null,

    var startDate: List<Int>? = null,

    var duration: Int? = null,

    var type: CalendarOptionsType? = null,

    var repeating: Boolean? = null,

    var recurrence: String? = null,

    var frequency: Frequency? = null,

    var interval: Int? = null,

    var repeatsOnNthDayOfMonth: RepeatsOnNthDayOfMonth? = null,

    var firstReminder: Int? = null,

    var secondReminder: Int? = null,

    var recurringDates: List<List<Int>>? = null,

    var nextTenRecurringDates: List<List<Int>>? = null,

    var humanReadable: String? = null,

    var createdDate: List<Int>? = null,

    var lastUpdatedDate: List<Int>? = null,

    var createdByUserId: Int? = null,

    var createdByUsername: String? = null,

    var lastUpdatedByUserId: Int? = null,

    var lastUpdatedByUsername: String? = null
) : Parcelable