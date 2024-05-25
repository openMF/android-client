package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 2/25/2016.
 */
@Parcelize
data class RepaymentFrequencyTypeOptions(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable