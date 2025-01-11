/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.collectionsheets.AttendanceTypeOption
import com.mifos.core.objects.collectionsheets.SavingsProduct
import com.mifos.core.objects.organisations.LoanProducts
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class CollectionSheetResponse(
    var attendanceTypeOptions: List<AttendanceTypeOption> = ArrayList(),

    var dueDate: IntArray? = null,

    var groups: List<GroupCollectionSheet> = ArrayList(),

    var loanProducts: List<LoanProducts> = ArrayList(),

    var paymentTypeOptions: List<com.mifos.core.entity.PaymentTypeOption> = ArrayList(),

    var savingsProducts: List<SavingsProduct> = ArrayList(),
) : Parcelable
