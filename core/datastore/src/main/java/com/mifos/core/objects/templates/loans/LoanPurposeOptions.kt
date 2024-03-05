package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class LoanPurposeOptions(
    var id: Int? = null,

    var name: String? = null,

    var position: Int? = null,

    var description: String? = null,

    var isActive: Boolean? = null
) : Parcelable