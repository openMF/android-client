/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import android.os.Parcelable
import com.mifos.room.entities.noncore.BulkRepaymentTransactions
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 11-07-2017.
 */

@Parcelize
data class IndividualCollectionSheetPayload(
    var bulkRepaymentTransactions: ArrayList<BulkRepaymentTransactions> = ArrayList(),
    var actualDisbursementDate: String? = null,
    var bulkDisbursementTransactions: List<BulkRepaymentTransactions> = ArrayList(),
    var bulkSavingsDueTransactions: List<BulkRepaymentTransactions> = ArrayList(),
    var dateFormat: String = "dd MMMM YYYY",
    var locale: String = "en",
    var transactionDate: String? = null,
) : Parcelable
