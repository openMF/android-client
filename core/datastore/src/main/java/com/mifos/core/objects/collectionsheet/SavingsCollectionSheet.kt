package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.accounts.savings.Currency
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 31-07-17.
 */
@Parcelize
data class SavingsCollectionSheet(
    //The accountId is of String type only. It's not a mistake.
    var accountId: String? = null,

    var accountStatusId: Int = 0,

    var currency: Currency? = null,

    var depositAccountType: String? = null,

    var dueAmount: Int = 0,

    var productId: Int = 0,

    var productName: String? = null,

    var savingsId: Int = 0
) : Parcelable