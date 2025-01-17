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
import com.mifos.room.entities.noncore.BulkRepaymentTransactions
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
data class ProductiveCollectionSheetPayload(
    var bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList(),

    var calendarId: Int? = 0,

    var dateFormat: String? = "dd MMMM yyyy",

    var locale: String? = "en",

    var transactionDate: String? = null,
) : Parcelable