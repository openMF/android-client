package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class CalendarOptions(
    var id: Int,
    var calendarInstanceId: Int,
    var entityId: Int,
    var entityType: EntityType,
    var title: String,
    var startDate: List<Int>,
    var duration: Int,
    var type: CalendarOptionsType,
    var repeating: Boolean,
    var recurrence: String,
    var frequency: Frequency,
    var interval: Int,
    var repeatsOnNthDayOfMonth: RepeatsOnNthDayOfMonth,
    var firstReminder: Int,
    var secondReminder: Int,
    var recurringDates: List<List<Int>>,
    var nextTenRecurringDates: List<List<Int>>,
    var humanReadable: String,
    var createdDate: List<Int>,
    var lastUpdatedDate: List<Int>,
    var createdByUserId: Int,
    var createdByUsername: String,
    var lastUpdatedByUserId: Int,
    var lastUpdatedByUsername: String
) : Parcelable