package com.mifos.objects.client

import android.os.Parcelable
import com.mifos.api.local.MifosBaseModel
import com.mifos.api.local.MifosDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 05/07/16.
 */
@Parcelize
@Table(database = MifosDatabase::class, name = "ClientChargeCurrency")
@ModelContainer
data class Currency(
    @PrimaryKey
    var code: String? = null,

    @Column
    var name: String? = null,

    @Column
    var decimalPlaces: Int? = null,

    @Column
    var inMultiplesOf: Int? = null,

    @Column
    var displaySymbol: String? = null,

    @Column
    var nameCode: String? = null,

    @Column
    var displayLabel: String? = null
) : MifosBaseModel(), Parcelable