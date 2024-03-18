package com.mifos.core.objects.templates.loans

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class FundOptions(
    var id: Int? = null,

    var name: String? = null
) : Parcelable