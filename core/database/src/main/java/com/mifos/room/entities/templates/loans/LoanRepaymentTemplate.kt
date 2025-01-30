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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.objects.template.loan.Currency
import com.mifos.core.objects.template.loan.Type
import com.mifos.room.entities.PaymentTypeOption
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "LoanRepaymentTemplate")
data class LoanRepaymentTemplate(
    @PrimaryKey(autoGenerate = true)
    val loanId: Int? = null,

    val type: Type? = null,

    val date: MutableList<Int>? = null,

    val currency: Currency? = null,

    val amount: Double? = null,

    val principalPortion: Double? = null,

    val interestPortion: Double? = null,

    val feeChargesPortion: Double? = null,

    val penaltyChargesPortion: Double? = null,

    val paymentTypeOptions: MutableList<PaymentTypeOption>? = null,
) : Parcelable
