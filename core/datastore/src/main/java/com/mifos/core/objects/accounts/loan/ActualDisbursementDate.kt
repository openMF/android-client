package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * This Model is for saving the ActualDisbursementDate of LoanWithAssociations's Timeline
 * This Model is only for Database use.
 * Created by Rajan Maurya on 26/07/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class ActualDisbursementDate(
    @PrimaryKey
    var loanId: Int? = null,

    @Column
    var year: Int? = null,

    @Column
    var month: Int? = null,

    @Column
    var date: Int? = null
) : MifosBaseModel(), Parcelable