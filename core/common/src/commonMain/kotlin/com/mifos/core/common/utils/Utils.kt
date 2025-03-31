package com.mifos.core.common.utils

import kotlinx.datetime.LocalDate

object Utils {
    fun getStringOfDate(dateObj: List<Int?>): String {
        val year = dateObj.getOrNull(0) ?: return "Invalid Date"
        val month = dateObj.getOrNull(1) ?: return "Invalid Date"
        val day = dateObj.getOrNull(2) ?: return "Invalid Date"

        return try {
            val date = LocalDate(year, month, day)
            val monthFormatted = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
            "${date.dayOfMonth} $monthFormatted ${date.year}"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
