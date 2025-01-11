/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.checkerinboxtask

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class CheckerTask(
    var id: Int,
    var madeOnDate: Long,
    var processingResult: String,
    var maker: String,
    var actionName: String,
    var entityName: String,
    var resourceId: String,
) : Parcelable {

    var selectedFlag = false

    fun getDate(): String {
        val date = Date(madeOnDate)
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        return dateFormat.format(date)
    }

    fun getTimeStamp(): Timestamp {
        return Timestamp(madeOnDate)
    }
}
