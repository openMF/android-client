package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 06-07-2017.
 */
@Parcelize
data class IndividualCollectionSheet(
    var dueDate: IntArray? = null,

    var clients: ArrayList<ClientCollectionSheet>? = null,

    var paymentTypeOptions: ArrayList<PaymentTypeOptions>? = null
) : Parcelable