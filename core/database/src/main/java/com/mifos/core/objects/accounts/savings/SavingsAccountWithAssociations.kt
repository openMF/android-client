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
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.`object`.account.saving.InterestCalculationDaysInYearType
import com.mifos.core.`object`.account.saving.InterestCalculationType
import com.mifos.core.`object`.account.saving.InterestCompoundingPeriodType
import com.mifos.core.`object`.account.saving.InterestPostingPeriodType
import com.mifos.core.`object`.account.saving.LockinPeriodFrequencyType
import com.mifos.core.`object`.account.saving.Timeline
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class SavingsAccountWithAssociations(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var accountNo: Int? = null,

    var clientId: Int? = null,

    @Column
    var clientName: String? = null,

    var savingsProductId: Int? = null,

    @Column
    var savingsProductName: String? = null,

    var fieldOfficerId: Int? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
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

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var summary: Summary? = null,

    var transactions: List<Transaction> = ArrayList(),

    var charges: List<Charge?> = ArrayList(),

) : MifosBaseModel(), Parcelable
