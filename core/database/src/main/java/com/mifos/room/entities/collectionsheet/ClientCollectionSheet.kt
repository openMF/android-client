/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.collectionsheets.AttendanceTypeOption
import com.mifos.core.objects.collectionsheets.LoanCollectionSheet
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 06-07-2017.
 */
@Parcelize
data class ClientCollectionSheet(
    var clientId: Int = 0,

    var clientName: String? = null,

    var loans: ArrayList<LoanCollectionSheet>? = null,

    var attendanceType: AttendanceTypeOption? = null,

    var savings: ArrayList<SavingsCollectionSheet> = ArrayList(),
) : Parcelable
