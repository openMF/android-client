/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.collectionsheet

import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.model.objects.collectionsheets.BulkSavingsDueTransaction
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.client.ClientsAttendance
import com.mifos.room.entities.noncore.BulkRepaymentTransactions
import kotlinx.serialization.Serializable

/**
 * Created by Tarun on 31-07-17.
 */
@Parcelize
@Serializable
data class CollectionSheetPayload(
    var actualDisbursementDate: String? = null,

    var bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList(),

    @IgnoredOnParcel
    var bulkSavingsDueTransactions: MutableList<BulkSavingsDueTransaction> = ArrayList(),

    var calendarId: Int? = 0,

    var clientsAttendance: MutableList<ClientsAttendance> = ArrayList(),

    var dateFormat: String = ApiDateFormatter.DATE_FORMAT,

    var locale: String = ApiDateFormatter.LOCALE,

    var transactionDate: String? = null,

    var accountNumber: String? = null,

    var bankNumber: String? = null,

    var checkNumber: String? = null,

    var paymentTypeId: Int = 0,

    var receiptNumber: String? = null,

    var routingCode: String? = null,
) : Parcelable
