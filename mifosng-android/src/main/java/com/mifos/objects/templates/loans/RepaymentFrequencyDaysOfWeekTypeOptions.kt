package com.mifos.objects.templates.loans

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
    var id: Int,
    var code: String,
    var value: String
) : Parcelable {
    override fun toString(): String {
        return "RepaymentFrequencyDayOfWeekTypeOptions{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}'
    }
}