package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 *
 * Created by Rajan Maurya on 17/08/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class SavingsTransactionDate(
    @PrimaryKey
    var transactionId: Int? = null,

    @Column
    var year: Int? = null,

    @Column
    var month: Int? = null,

    @Column
    var day: Int? = null
) : MifosBaseModel(), Parcelable