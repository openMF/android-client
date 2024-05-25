package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 31-07-17.
 */
@Parcelize
data class SavingsProduct(
    var isAllowOverdraft: Boolean = false,

    var depositAccountType: String? = null,

    var isEnforceMinRequiredBalance: Boolean = false,

    var id: Int = 0,

    var name: String? = null,

    var isWithHoldTax: Boolean = false,

    var isWithdrawalFeeForTransfers: Boolean = false
) : Parcelable