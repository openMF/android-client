/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Table(database = MifosDatabase::class, name = "SavingsAccountSummary")
@ModelContainer
class Summary(
    @PrimaryKey
    @Transient
    var savingsId: Int? = null,

    var currency: Currency? = null,

    @Column
    var totalDeposits: Double? = null,

    @Column
    var accountBalance: Double? = null,

    @Column
    var totalWithdrawals: Double? = null,

    @Column
    var totalInterestEarned: Double? = null
) : MifosBaseModel(), Parcelable