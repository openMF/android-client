/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.loans

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "LoanRepaymentTemplate")
data class LoanRepaymentTemplateEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val loanId: Int = 0,

    val type: LoanType? = null,

    val date: List<Int>? = null,

    val currency: SavingAccountCurrencyEntity? = null,

    val amount: Double? = null,

    val principalPortion: Double? = null,

    val interestPortion: Double? = null,

    val feeChargesPortion: Double? = null,

    val penaltyChargesPortion: Double? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity>? = null,
) : Parcelable
