package com.mifos.core.objects.organisation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Parcelize
data class RepaymentFrequencyType(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable