/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import java.text.DateFormat
import java.util.Calendar
import java.util.TimeZone

object Utils {
    // Create a single DateFormat instance to reuse
    private val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)

    fun getStringOfDate(dateObj: List<Int?>?): String {
        if (dateObj == null) {
            return ""
        }

        return synchronized(dateFormat) {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            if (dateObj.getOrNull(0) != null) {
                calendar.set(Calendar.YEAR, dateObj[0]!!)
            }
            if (dateObj.getOrNull(1) != null) {
                calendar.set(Calendar.MONTH, dateObj[1]!! - 1)
            }
            if (dateObj.getOrNull(2) != null) {
                calendar.set(Calendar.DAY_OF_MONTH, dateObj[2]!!)
            }

            dateFormat.format(calendar.time)
        }
    }
}
