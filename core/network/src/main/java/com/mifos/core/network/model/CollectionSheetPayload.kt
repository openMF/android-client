/*
 * This project is licensed under the open source MPL V2.
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