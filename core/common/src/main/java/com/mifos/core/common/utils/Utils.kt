package com.mifos.core.common.utils

import java.text.DateFormat
import java.util.Calendar
import java.util.TimeZone

object Utils {
    fun getStringOfDate(dateObj: List<Int?>): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateObj.getOrNull(0)?.let { year ->
            calendar.set(Calendar.YEAR, year)
        }
        dateObj.getOrNull(1)?.let { month ->
            calendar.set(Calendar.MONTH, month - 1)
        }
        dateObj.getOrNull(2)?.let { day ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return dateFormat.format(calendar.time)
    }
}