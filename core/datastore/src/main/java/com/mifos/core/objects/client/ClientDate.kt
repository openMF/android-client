package com.mifos.core.objects.client

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 04/07/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class ClientDate(
    @PrimaryKey
    var clientId: Long = 0,

    @PrimaryKey
    var chargeId: Long = 0,

    @Column
    var day: Int = 0,

    @Column
    var month: Int = 0,

    @Column
    var year: Int = 0
) : MifosBaseModel(), Parcelable