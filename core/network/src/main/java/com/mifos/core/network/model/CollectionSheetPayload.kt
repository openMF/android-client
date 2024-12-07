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

import com.google.gson.Gson
import com.mifos.core.model.BulkRepaymentTransactions

class CollectionSheetPayload : Payload() {
    var actualDisbursementDate: String? = null
    lateinit var bulkDisbursementTransactions: IntArray
    lateinit var bulkRepaymentTransactions: Array<BulkRepaymentTransactions>
    lateinit var clientsAttendance: Array<String>
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
