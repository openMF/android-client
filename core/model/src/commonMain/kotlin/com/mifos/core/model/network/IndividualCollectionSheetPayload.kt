/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.network

import com.mifos.core.model.objects.loan.BulkRepaymentTransactions
import com.mifos.core.model.utils.ApiDateFormatter
import kotlinx.serialization.Serializable

/**
 * Created by Tarun on 11-07-2017.
 */

@Serializable
data class IndividualCollectionSheetPayload(
    var bulkRepaymentTransactions: ArrayList<BulkRepaymentTransactions> = ArrayList(),
    var actualDisbursementDate: String? = null,
    var bulkDisbursementTransactions: List<BulkRepaymentTransactions> = ArrayList(),
    var bulkSavingsDueTransactions: List<BulkRepaymentTransactions> = ArrayList(),
    var dateFormat: String = ApiDateFormatter.DATE_FORMAT,
    var locale: String = ApiDateFormatter.LOCALE,
    var transactionDate: String? = null,
)
