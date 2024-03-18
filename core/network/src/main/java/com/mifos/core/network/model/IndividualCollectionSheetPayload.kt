package com.mifos.core.network.model

import android.os.Parcelable
import com.mifos.core.model.BulkRepaymentTransactions
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
    var transactionDate: String? = null
) : Parcelable