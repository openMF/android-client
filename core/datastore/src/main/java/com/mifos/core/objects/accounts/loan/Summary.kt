/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
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
 * This Model is saving the Summary of the Loans according to their Id's
 *
 */
@Parcelize
@Table(database = MifosDatabase::class, name = "LoansAccountSummary")
@ModelContainer
data class Summary(
    @PrimaryKey
    @Transient
    var loanId: Int? = null,

    var currency: Currency? = null,

    @Column
    var principalDisbursed: Double? = null,

    @Column
    var principalPaid: Double? = null,

    var principalWrittenOff: Double? = null,

    @Column
    var principalOutstanding: Double? = null,

    var principalOverdue: Double? = null,

    @Column
    var interestCharged: Double? = null,

    @Column
    var interestPaid: Double? = null,

    var interestWaived: Double? = null,

    var interestWrittenOff: Double? = null,

    @Column
    var interestOutstanding: Double? = null,

    var interestOverdue: Double? = null,

    @Column
    var feeChargesCharged: Double? = null,

    var feeChargesDueAtDisbursementCharged: Double? = null,

    @Column
    var feeChargesPaid: Double? = null,

    var feeChargesWaived: Double? = null,

    var feeChargesWrittenOff: Double? = null,

    @Column
    var feeChargesOutstanding: Double? = null,

    var feeChargesOverdue: Double? = null,

    @Column
    var penaltyChargesCharged: Double? = null,

    @Column
    var penaltyChargesPaid: Double? = null,

    var penaltyChargesWaived: Double? = null,

    var penaltyChargesWrittenOff: Double? = null,

    @Column
    var penaltyChargesOutstanding: Double? = null,

    var penaltyChargesOverdue: Double? = null,

    @Column
    var totalExpectedRepayment: Double? = null,

    @Column
    var totalRepayment: Double? = null,

    var totalExpectedCostOfLoan: Double? = null,

    var totalCostOfLoan: Double? = null,

    var totalWaived: Double? = null,

    var totalWrittenOff: Double? = null,

    @Column
    var totalOutstanding: Double? = null,

    @Column
    var totalOverdue: Double? = null,

    var overdueSinceDate: List<Int>? = null
) : MifosBaseModel(), Parcelable