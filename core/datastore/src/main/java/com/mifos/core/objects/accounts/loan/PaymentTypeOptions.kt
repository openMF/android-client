package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 3/3/2016.
 */
@Parcelize
data class PaymentTypeOptions(
    var id: Int,

    var name: String?
) : Parcelable