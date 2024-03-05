package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 31-07-17.
 */
@Parcelize
data class BulkSavingsDueTransaction(
    var savingsId: Int,

    var transactionAmount: String?
) : Parcelable