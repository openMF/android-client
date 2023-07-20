package com.mifos.objects.collectionsheet

import android.os.Parcelable
import com.mifos.api.model.BulkRepaymentTransactions
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

    var transactionDate: String? = null
) : Parcelable