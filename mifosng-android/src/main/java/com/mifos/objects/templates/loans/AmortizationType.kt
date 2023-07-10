package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class AmortizationType(
    @field:SerializedName("id")
    var id: Int = 0,
    @field:SerializedName("code")
    var code: String = "",
    @field:SerializedName("value")
    var value: String = ""
) : Parcelable {

    override fun toString(): String {
        return "AmortizationType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}'
    }
}





