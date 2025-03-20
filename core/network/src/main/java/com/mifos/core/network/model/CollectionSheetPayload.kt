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

import com.mifos.room.entities.noncore.BulkRepaymentTransactions
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class CollectionSheetPayload : Payload() {
    var actualDisbursementDate: String? = null
    var bulkDisbursementTransactions: List<Int> = emptyList()
    var bulkRepaymentTransactions: List<BulkRepaymentTransactions> = emptyList()
    var clientsAttendance: List<String> = emptyList()

    override fun toString(): String {
        return Json.encodeToString(this)
    }
}
