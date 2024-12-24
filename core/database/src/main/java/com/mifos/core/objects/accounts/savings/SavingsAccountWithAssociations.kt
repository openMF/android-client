/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("SavingAccountWithAssociations")
data class SavingsAccountWithAssociations(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("accountNo")
    var accountNo: Int? = null,

    var clientId: Int? = null,

    @ColumnInfo("clientName")
    var clientName: String? = null,

    var savingsProductId: Int? = null,

    @ColumnInfo("savingsProductName")
    var savingsProductName: String? = null,

    var fieldOfficerId: Int? = null,

    @ColumnInfo("status")
    @Embedded
    var status: Status? = null,

    var timeline: Timeline? = null,

    var currency: Currency? = null,

    var nominalAnnualInterestRate: Double? = null,

    var interestCompoundingPeriodType: InterestCompoundingPeriodType? = null,

    var interestPostingPeriodType: InterestPostingPeriodType? = null,

    var interestCalculationType: InterestCalculationType? = null,

    var interestCalculationDaysInYearType: InterestCalculationDaysInYearType? = null,

    var minRequiredOpeningBalance: Double? = null,

    var lockinPeriodFrequency: Int? = null,

    var lockinPeriodFrequencyType: LockinPeriodFrequencyType? = null,

    var withdrawalFeeForTransfers: Boolean? = null,

    var allowOverdraft: Boolean? = null,

    var enforceMinRequiredBalance: Boolean? = null,

    var withHoldTax: Boolean? = null,

    var lastActiveTransactionDate: List<Int?> = ArrayList(),

    var dormancyTrackingActive: Boolean? = null,

    var overdraftLimit: Int? = null,

    @ColumnInfo("summary")
    @Embedded
    var summary: Summary? = null,

    var transactions: List<Transaction> = ArrayList(),

    var charges: List<Charge?> = ArrayList(),

) : MifosBaseModel(), Parcelable
