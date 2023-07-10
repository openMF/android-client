package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class LoanCollateralOptions(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("position")
    var position: Int,
    @SerializedName("description")
    var description: String,
    @SerializedName("isActive")
    var isActive: Boolean
) : Parcelable {
    override fun toString(): String {
        return "LoanCollateralOptions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}'
    }
}