package com.mifos.core.objects.templates.loans

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class RepaymentFrequencyDaysOfWeekTypeOptions(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable