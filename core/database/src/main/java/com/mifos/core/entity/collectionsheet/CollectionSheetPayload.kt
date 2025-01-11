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
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.model.ClientsAttendance
import com.mifos.core.objects.collectionsheets.BulkSavingsDueTransaction
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 31-07-17.
 */
@Parcelize
data class CollectionSheetPayload(
    var actualDisbursementDate: String? = null,

    var bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList(),

    var bulkSavingsDueTransactions: MutableList<BulkSavingsDueTransaction> = ArrayList(),

    var calendarId: Int? = 0,

    var clientsAttendance: MutableList<ClientsAttendance> = ArrayList(),

    var dateFormat: String = "dd MMMM yyyy",

    var locale: String = "en",

    var transactionDate: String? = null,

    var accountNumber: String? = null,

    var bankNumber: String? = null,

    var checkNumber: String? = null,

    var paymentTypeId: Int = 0,

    var receiptNumber: String? = null,

    var routingCode: String? = null,
) : Parcelable
