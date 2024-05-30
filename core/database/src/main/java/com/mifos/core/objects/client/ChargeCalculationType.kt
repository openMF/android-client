package com.mifos.core.objects.client

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/07/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class ChargeCalculationType(
    @JvmField
    @PrimaryKey
    @SerializedName("id")
    var id: Int? = null,

    @JvmField
    @Column
    @SerializedName("code")
    var code: String? = null,

    @JvmField
    @Column
    @SerializedName("value")
    var value: String? = null
) : MifosBaseModel(), Parcelable