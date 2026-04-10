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
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.noncore.BulkRepaymentTransactions
import kotlinx.serialization.Serializable

/**
 * Created by Tarun on 25-07-2017.
 */
@Parcelize
@Serializable
data class ProductiveCollectionSheetPayload(
    var bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList(),

    var calendarId: Int? = 0,

    var dateFormat: String? = ApiDateFormatter.DATE_FORMAT,

    var locale: String? = ApiDateFormatter.LOCALE,

    var transactionDate: String? = null,
) : Parcelable
