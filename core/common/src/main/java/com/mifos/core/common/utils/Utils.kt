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
