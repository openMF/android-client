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
import com.mifos.core.objects.template.loan.Currency
import com.mifos.core.objects.template.loan.Type
import com.mifos.room.entities.PaymentTypeOption
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "LoanRepaymentTemplate")
data class LoanRepaymentTemplate(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "loanId")
    var loanId: Int? = null,

    @ColumnInfo(name = "type")
    var type: Type? = null,

    @ColumnInfo(name = "date")
    var date: MutableList<Int>? = null,

    @ColumnInfo(name = "currency")
    var currency: Currency? = null,

    @ColumnInfo(name = "amount")
    var amount: Double? = null,

    @ColumnInfo(name = "principalPortion")
    var principalPortion: Double? = null,

    @ColumnInfo(name = "interestPortion")
    var interestPortion: Double? = null,

    @ColumnInfo(name = "feeChargesPortion")
    var feeChargesPortion: Double? = null,

    @ColumnInfo(name = "penaltyChargesPortion")
    var penaltyChargesPortion: Double? = null,

    @ColumnInfo(name = "paymentTypeOptions")
    var paymentTypeOptions: MutableList<PaymentTypeOption>? = null,
) : Parcelable
