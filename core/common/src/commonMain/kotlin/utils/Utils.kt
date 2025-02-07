/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package utils

import java.text.DateFormat
import java.util.Calendar
import java.util.TimeZone
import kotlin.collections.getOrNull
import kotlin.let

object Utils {
    fun getStringOfDate(dateObj: List<Int?>): String {
        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        dateObj.getOrNull(0)?.let { year ->
            java.util.Calendar.set(java.util.Calendar.YEAR, year)
        }
        dateObj.getOrNull(1)?.let { month ->
            java.util.Calendar.set(java.util.Calendar.MONTH, month - 1)
        }
        dateObj.getOrNull(2)?.let { day ->
            java.util.Calendar.set(java.util.Calendar.DAY_OF_MONTH, day)
        }
        val dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM)
        return java.text.DateFormat.format(java.util.Calendar.getTime)
    }
}
