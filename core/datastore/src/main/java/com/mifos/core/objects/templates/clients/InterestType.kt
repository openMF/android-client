package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 06/07/16.
 */
@Table(database = MifosDatabase::class, name = "ClientTemplateInterest")
@ModelContainer
@Parcelize
data class InterestType(
    @PrimaryKey
    var id: Int = 0,

    @Column
    var code: String = "",

    @Column
    var value: String = ""
) : MifosBaseModel(), Parcelable