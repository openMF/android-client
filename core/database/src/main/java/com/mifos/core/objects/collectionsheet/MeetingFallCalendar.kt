/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.Status
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class MeetingFallCalendar(
    // It's not a mistake. This AccountNo field DOES expect a String.
    var accountNo: String? = null,

    var activationDate: IntArray,

    var isActive: Boolean = false,

    var collectionMeetingCalendar: CollectionMeetingCalendar? = null,

    var hierarchy: String? = null,

    var id: Int = 0,

    var installmentDue: Int = 0,

    var name: String? = null,

    var officeId: Int = 0,

    var staffId: Int = 0,

    var staffName: String? = null,

    var status: Status? = null,

    var totalCollected: Int = 0,

    var totalOverdue: Int = 0,

    var totaldue: Int = 0,
) : Parcelable
